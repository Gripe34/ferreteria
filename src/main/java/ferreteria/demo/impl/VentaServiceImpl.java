package ferreteria.demo.impl;

import ferreteria.demo.dto.DetalleVentaDTO;
import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.dto.VentasDashboardDTO;
import ferreteria.demo.repository.*;
import ferreteria.demo.service.MovimientoInventarioService;
import ferreteria.demo.service.VentaService;
import ferreteria.demo.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioService movimientoInventarioService;
    private final ModelMapper modelMapper;

    public VentaServiceImpl(
            VentaRepository ventaRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository,
            MovimientoInventarioService movimientoInventarioService,
            ModelMapper modelMapper) {

        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoInventarioService = movimientoInventarioService;
        this.modelMapper = modelMapper;
    }

    // 🔥 Mapeador privado, corregido para robustez y factura
    private VentaDTO convertToDto(Venta venta) {
        VentaDTO ventaDTO = modelMapper.map(venta, VentaDTO.class);

        // 1. Solución de Robustez: Asegurar que fechaRegistro NUNCA sea null para el PDF.
        if (ventaDTO.getFechaRegistro() == null) {
            ventaDTO.setFechaRegistro(LocalDateTime.now());
        }

        // 2. Población de Nombres
        if (venta.getCliente() != null) {
            ventaDTO.setClienteNombre(venta.getCliente().getUsername());
        }

        // 3. Mapeo del Método de Pago desde la Entity (Necesario para Clasificación y PDF)
        if (venta.getMetodoPago() != null) {
            ventaDTO.setMetodoPago(venta.getMetodoPago());
        } else {
            // Default para manejo robusto de datos antiguos
            ventaDTO.setMetodoPago("WEB");
        }

        // 4. Mapeo de los detalles (Evitar NullPointerException si la lista es lazy y no se cargó)
        if (venta.getDetalles() != null) {
            List<DetalleVentaDTO> detalleDTOs = venta.getDetalles().stream().map(detalle -> {
                DetalleVentaDTO dto = modelMapper.map(detalle, DetalleVentaDTO.class);
                dto.setProductoNombre(detalle.getProducto().getNombre());
                return dto;
            }).collect(Collectors.toList());
            ventaDTO.setDetalles(detalleDTOs);
        } else {
            ventaDTO.setDetalles(new ArrayList<>());
        }

        return ventaDTO;
    }


    // --- MÉTODOS PÚBLICOS ---

    @Override
    public List<VentaDTO> findAllVentas() {
        return ventaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public VentaDTO findById(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + idVenta));

        return convertToDto(venta);
    }

    @Override
    @Transactional
    public VentaDTO crearVenta(VentaDTO ventaDTO, Long vendedorId) {

        // 1. Validar Cliente y Vendedor
        Usuario cliente = usuarioRepository.findById(ventaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + ventaDTO.getClienteId()));

        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con id: " + vendedorId));

        BigDecimal totalCalculado = BigDecimal.ZERO;
        List<DetalleVenta> detallesParaGuardar = new ArrayList<>();

        // --- Paso 2: Iterar y Calcular el Total ---
        for (DetalleVentaDTO detalleDTO : ventaDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + detalleDTO.getProductoId()));

            totalCalculado = totalCalculado.add(producto.getPrecio().multiply(new BigDecimal(detalleDTO.getCantidad())));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detallesParaGuardar.add(detalle);
        }

        // --- Paso 3: Crear el Encabezado y ASIGNAR Campos Obligatorios ---
        Venta venta = new Venta();

        // Asignación de fecha/hora para la DB y auditoría
        venta.setFecha(LocalDate.now());
        venta.setFechaRegistro(LocalDateTime.now());
        venta.setCliente(cliente);
        venta.setTotal(totalCalculado);

        // ASIGNACIÓN CRÍTICA: Guardar el método de pago (EFECTIVO/TARJETA)
        if (ventaDTO.getMetodoPago() != null) {
            venta.setMetodoPago(ventaDTO.getMetodoPago());
        } else {
            venta.setMetodoPago("EFECTIVO");
        }

        Venta savedVenta = ventaRepository.save(venta);

        // --- Paso 4: Persistir Movimientos y Relacionar Detalles ---
        for (DetalleVenta detalle : detallesParaGuardar) {

            detalle.setVenta(savedVenta);
            savedVenta.getDetalles().add(detalle);

            MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO();
            movimientoDTO.setProductoId(detalle.getProducto().getId());
            movimientoDTO.setCantidad(-detalle.getCantidad()); // SALIDA
            movimientoDTO.setTipoMovimiento(TipoMovimiento.SALIDA_VENTA);
            movimientoDTO.setDocumentoOrigenId(savedVenta.getId());
            movimientoDTO.setUsuarioId(vendedorId); // ID del vendedor

            movimientoInventarioService.registrarMovimiento(movimientoDTO);
        }

        Venta updatedVenta = ventaRepository.save(savedVenta);

        // POBLAR DTO DE RESPUESTA CON NOMBRE DEL VENDEDOR Y USAR MAPPER
        VentaDTO finalDto = convertToDto(updatedVenta);
        finalDto.setVendedorNombre(vendedor.getUsername());

        return finalDto;
    }

    @Override
    @Transactional
    public VentaDTO crearVentaInternaDesdePedido(Pedido pedido, Long sistemaUserId) {

        // 1. Validar el usuario de sistema
        usuarioRepository.findById(sistemaUserId)
                .orElseThrow(() -> new RuntimeException("Usuario de sistema/administrador no encontrado con id: " + sistemaUserId));

        BigDecimal totalCalculado = pedido.getTotal();

        // --- Paso 2: Crear el Encabezado de la Venta (a partir del Pedido) ---
        Venta venta = new Venta();

        venta.setFecha(LocalDate.now());
        venta.setFechaRegistro(LocalDateTime.now());
        venta.setCliente(pedido.getCliente());
        venta.setTotal(totalCalculado);

        // Asignación de método de pago para ventas web
        venta.setMetodoPago("WEB");

        Venta savedVenta = ventaRepository.save(venta);

        // --- Paso 3: Persistir Movimientos y Relacionar Detalles (usando DetallePedido) ---
        for (DetallePedido detallePedido : pedido.getDetalles()) {

            // 3.1 Crear DetalleVenta
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(savedVenta);
            detalleVenta.setProducto(detallePedido.getProducto());
            detalleVenta.setCantidad(detallePedido.getCantidad());
            detalleVenta.setPrecioUnitario(detallePedido.getPrecioUnitarioVenta());

            savedVenta.getDetalles().add(detalleVenta);

            // 3.2 Registrar Movimiento de Inventario (Salida)
            MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO();
            movimientoDTO.setProductoId(detallePedido.getProducto().getId());
            movimientoDTO.setCantidad(-detallePedido.getCantidad()); // SALIDA
            movimientoDTO.setTipoMovimiento(TipoMovimiento.SALIDA_PEDIDO_WEB);
            movimientoDTO.setDocumentoOrigenId(savedVenta.getId());
            movimientoDTO.setUsuarioId(sistemaUserId); // ID del usuario de sistema

            movimientoInventarioService.registrarMovimiento(movimientoDTO);
        }

        Venta updatedVenta = ventaRepository.save(savedVenta);

        // 4. Devolver el DTO
        return convertToDto(updatedVenta);
    }

    // 🔥🔥🔥 IMPLEMENTACIÓN DEL MÉTODO FALTANTE (SOLUCIÓN HISTORIAL DE COMPRAS) 🔥🔥🔥
    @Override
    public List<VentaDTO> findVentasByClienteUsername(String username) {

        // 1. Buscar el usuario (cliente) por su username
        Usuario cliente = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario (Cliente) no encontrado: " + username));

        // 2. Buscar todas las ventas asociadas a ese cliente ID
        // Nota: Asume que tienes un método findByCliente en tu VentaRepository.
        // Si no lo tienes, deberás agregarlo (ej: List<Venta> findByClienteId(Long clienteId);)
        List<Venta> ventas = ventaRepository.findByCliente(cliente);

        // 3. Mapear las entidades de Venta a VentaDTO y devolver
        return ventas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }




    @Override
    public VentasDashboardDTO getVentasClasificadas() {

        List<VentaDTO> todasLasVentas = findAllVentas();
        VentasDashboardDTO dashboard = new VentasDashboardDTO();

        List<VentaDTO> web = new ArrayList<>();
        List<VentaDTO> efectivo = new ArrayList<>();
        List<VentaDTO> tarjeta = new ArrayList<>();

        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (VentaDTO venta : todasLasVentas) {
            String metodo = Optional.ofNullable(venta.getMetodoPago())
                    .orElse("WEB_FALLBACK") // Assign a fallback value for nulls
                    .toUpperCase(); // Convert to uppercase for robust comparison

            totalGeneral = totalGeneral.add(venta.getTotal());

            // Process the classification
            if (metodo.equals("EFECTIVO")) {

                efectivo.add(venta);
            } else if (metodo.equals("TARJETA")) {

                tarjeta.add(venta);
            } else {
                web.add(venta);
            }
        }

        dashboard.setVentasWeb(web);
        dashboard.setVentasCajaEfectivo(efectivo);
        dashboard.setVentasCajaTarjeta(tarjeta);
        dashboard.setTotalGeneral(totalGeneral);

        return dashboard;
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDTO getVentaDetalleById(Long ventaId) {

        Venta ventaEntity = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Venta no encontrada con ID: " + ventaId
                ));

        return convertToVentaDetalleDTO(ventaEntity);
    }

    // --- MÉTODOS DE MAPEO AUXILIARES ---

    private VentaDTO convertToVentaDetalleDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();

        // Mapeo de campos de la cabecera de venta
        dto.setId(venta.getId());
        dto.setTotal(venta.getTotal());
        dto.setMetodoPago(venta.getMetodoPago());

        // Mapeo de Fecha: Usamos fechaRegistro (LocalDateTime) si existe, sino, usamos fecha (LocalDate) y convertimos
        if (venta.getFechaRegistro() != null) {
            dto.setFechaRegistro(venta.getFechaRegistro());
        } else if (venta.getFecha() != null) {
            dto.setFechaRegistro(venta.getFecha().atStartOfDay()); // Convierte LocalDate a LocalDateTime
        } else {
            dto.setFechaRegistro(null);
        }

        // ⚠️ VENDEDOR: Tu entidad Venta NO tiene una relación @ManyToOne con el Vendedor.
        // Por diseño de tu Entidad, no podemos obtener el nombre del vendedor desde aquí.
        // Se establece un valor por defecto o se asume que el DTO lo maneja.
        dto.setVendedorNombre("Vendedor (No Mapeado)");

        // Cliente: El cliente sí existe en la Entidad
        dto.setClienteNombre(venta.getCliente() != null ? venta.getCliente().getUsername() : "Consumidor Final");

        // Mapeo de Detalles: Entidad a DTO
        if (venta.getDetalles() != null) {
            List<DetalleVentaDTO> detallesDTO = venta.getDetalles().stream()
                    .map(this::mapDetalleToDTO)
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        }

        return dto;
    }

    private DetalleVentaDTO mapDetalleToDTO(DetalleVenta detalle) {
        DetalleVentaDTO detalleDTO = new DetalleVentaDTO();

        detalleDTO.setCantidad(detalle.getCantidad());
        detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());

        if (detalle.getProducto() != null) {
            // Usamos getNombre(), asumiendo que el getter existe en la Entidad Producto.
            detalleDTO.setProductoNombre(detalle.getProducto().getNombre());
        }

        return detalleDTO;
    }


    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> findAllVentasConDetalle() {
        // ⚠️ Importante: Asegúrate que esta consulta cargue los detalles también,
        // o usa un FetchType.EAGER en la relación OneToMany de Venta.
        List<Venta> ventasEntity = ventaRepository.findAll();

        // Aquí mapeas la lista de entidades a DTOs completos (como lo haces en getVentaDetalleById)
        return ventasEntity.stream()
                .map(this::convertToVentaDetalleDTO) // Reutiliza tu mapper existente
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> findVentasByClienteId(Long clienteId) {

        // 1. Necesitas este método en VentaRepository:
        // List<Venta> findByClienteId(Long clienteId);

        // 2. Ejecutar la consulta
        List<Venta> ventasEntity = ventaRepository.findByClienteId(clienteId);

        // 3. Mapear las entidades a DTOs (reutiliza tu mapper)
        return ventasEntity.stream()
                .map(this::convertToVentaDetalleDTO) // Asumo que tienes un mapper que devuelve el DTO completo
                .collect(Collectors.toList());
    }

}