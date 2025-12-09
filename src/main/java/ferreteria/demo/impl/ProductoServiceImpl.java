package ferreteria.demo.impl;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.dto.ProductoDTO;
import ferreteria.demo.dto.ProveedorDTO;
import ferreteria.demo.dto.PrecioHistoricoDTO;
import ferreteria.demo.entity.*;
import ferreteria.demo.repository.*;
import ferreteria.demo.service.MovimientoInventarioService; // Importado
import ferreteria.demo.service.ProductoService;
import ferreteria.demo.service.UsuarioService;
import ferreteria.demo.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.modelmapper.ModelMapper;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final PrecioHistoricoProductoRepository precioHistoricoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ProveedorRepository proveedorRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    
    // 🔥 CAMPO AÑADIDO: Necesario para registrar movimientos de stock
    private final MovimientoInventarioService movimientoInventarioService;

    // CONSTRUCTOR CORREGIDO Y COMPLETO
    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            MovimientoInventarioRepository movimientoRepository,
            PrecioHistoricoProductoRepository precioHistoricoRepository,
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            ProveedorRepository proveedorRepository,
            ModelMapper modelMapper,
            StorageService storageService,
            // 🔥 PARÁMETRO AÑADIDO: Rompe el ciclo si MovimientoServiceImpl no inyecta de vuelta
            MovimientoInventarioService movimientoInventarioService) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimientoRepository = movimientoRepository;
        this.precioHistoricoRepository = precioHistoricoRepository;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
        // 🔥 ASIGNACIÓN AÑADIDA
        this.movimientoInventarioService = movimientoInventarioService;
    }

    // --- MAPPERS ---
    private ProductoDTO convertToDto(Producto producto) {
        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
        dto.setCosto(Optional.ofNullable(producto.getCosto()).orElse(BigDecimal.ZERO));

        if (producto.getCategoria() != null) {
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        if (producto.getProveedor() != null) {
            dto.setProveedorId(producto.getProveedor().getId());
            dto.setProveedorNombre(producto.getProveedor().getNombre());
        }
        dto.setImageUrl(producto.getImageUrl());

        return dto;
    }

    private PrecioHistoricoDTO convertToHistoricoDto(PrecioHistoricoProducto historico) {
        PrecioHistoricoDTO dto = modelMapper.map(historico, PrecioHistoricoDTO.class);

        // Acceso seguro al Usuario (soluciona Cannot resolve method 'getUsuarioId')
        if (historico.getUsuario() != null) {
            dto.setUsuarioUsername(historico.getUsuario().getUsername());
        } else {
            // Si la relación es lazy o nula, intentamos buscarlo por ID directo
            if (historico.getUsuarioId() != null) {
                usuarioRepository.findById(historico.getUsuarioId()).ifPresent(u -> dto.setUsuarioUsername(u.getUsername()));
            }
            if (dto.getUsuarioUsername() == null) {
                dto.setUsuarioUsername("Sistema/Desconocido");
            }
        }
        return dto;
    }


    // --- LECTURA ---
    @Override public List<ProductoDTO> findAllProductos() { return productoRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList()); }
    @Override public ProductoDTO findById(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));
        return convertToDto(producto);
    }
    @Override public Optional<Producto> findEntityById(Long idProducto) { return productoRepository.findById(idProducto); }
    @Override public List<ProductoDTO> findAllProductosParaVenta() { return productoRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList()); }
    @Override public List<ProductoDTO> findAllProductosConDetalle() { return productoRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList()); }
    @Override public List<ProductoDTO> findAllProductosActivos() { return productoRepository.findAll().stream().filter(producto -> producto.getStock() > 0).map(this::convertToDto).collect(Collectors.toList()); }


    // --- HISTORIAL DE PRECIOS/COSTOS ---
    @Override
    @Transactional(readOnly = true)
    public List<PrecioHistoricoDTO> getHistorialPrecioPorProducto(Long productoId) {
        List<PrecioHistoricoProducto> historial = precioHistoricoRepository.findByProductoIdOrderByFechaCambioDesc(productoId);
        return historial.stream().map(this::convertToHistoricoDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoDTO updateProducto(Long idProducto, ProductoDTO productoDTO) {
        throw new UnsupportedOperationException("ERROR: El método updateProducto está obsoleto. Por favor, use editarProducto con el ID de usuario.");
    }


   
    @Override
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO, Long adminId, MultipartFile imagenArchivo) {
        if (productoDTO.getPrecio().compareTo(BigDecimal.ZERO) < 0 ||
                productoDTO.getCosto().compareTo(BigDecimal.ZERO) < 0 ||
                productoDTO.getStock() < 0) {
            throw new RuntimeException("Error: Los valores de Precio, Costo y Stock inicial no pueden ser negativos.");
        }

        try {
            // LÓGICA DE IMAGEN (Tu código original)
            if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
                String imageUrl = storageService.store(imagenArchivo);
                productoDTO.setImageUrl(imageUrl);
            }

            // BUSCAR ENTIDADES
            Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + productoDTO.getCategoriaId()));
            Proveedor proveedor = proveedorRepository.findById(productoDTO.getProveedorId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + productoDTO.getProveedorId()));

            // 🛑 VALIDACIÓN CRÍTICA: RESTRICCIÓN DE PROVEEDOR BLOQUEADO
            if (!proveedor.isActivo()) {
                throw new RuntimeException("ERROR DE NEGOCIO: No se puede crear el producto. El proveedor '" + proveedor.getNombre() + "' está BLOQUEADO.");
        }
        // ------------------------------------


        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setCosto(productoDTO.getCosto());
        producto.setStock(productoDTO.getStock());
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        producto.setImageUrl(productoDTO.getImageUrl());
        producto.setFechaModificacion(LocalDateTime.now());
        producto.setActivo(Boolean.TRUE); 
        producto.setBloqueado(Boolean.FALSE);

        Producto savedProducto = productoRepository.save(producto);

        // REGISTRO DE MOVIMIENTO INICIAL (Tu código original)
        if (productoDTO.getStock() > 0) {
            MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO();
            movimientoDTO.setProductoId(savedProducto.getId());
            movimientoDTO.setCantidad(productoDTO.getStock());
            movimientoDTO.setTipoMovimiento(TipoMovimiento.ENTRADA);
            movimientoDTO.setJustificacion("Stock inicial al crear producto.");
            movimientoDTO.setUsuarioId(adminId);

            movimientoInventarioService.registrarMovimiento(movimientoDTO); 
        }
        return convertToDto(savedProducto);
    } catch (Exception e) {
        // Tu manejo de excepciones original, con la adición del mensaje de error de negocio
        throw new RuntimeException("Error al crear producto o subir imagen: " + e.getMessage(), e);
    }
}   



    // --- EDICIÓN (IMAGEN Y AUDITORÍA) ---
    @Override
    @Transactional
    public ProductoDTO editarProducto(Long idProducto, ProductoDTO productoDTO, Long userId, MultipartFile imagenArchivo) {

        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));

        BigDecimal costoAnterior = Optional.ofNullable(productoExistente.getCosto()).orElse(BigDecimal.ZERO);
        BigDecimal precioAnterior = Optional.ofNullable(productoExistente.getPrecio()).orElse(BigDecimal.ZERO);

        // 1. BLINDAJE ANTI-NULL y Validación
        BigDecimal costoParaValidar = Optional.ofNullable(productoDTO.getCosto()).orElse(BigDecimal.ZERO);
        BigDecimal precioParaValidar = Optional.ofNullable(productoDTO.getPrecio()).orElse(BigDecimal.ZERO);
        Integer stockAnterior = productoExistente.getStock();
        Long proveedorAnteriorId = (productoExistente.getProveedor() != null) ? productoExistente.getProveedor().getId() : null;

        if (precioParaValidar.compareTo(BigDecimal.ZERO) < 0 || costoParaValidar.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Error: Los valores de Precio y Costo no pueden ser negativos al actualizar.");
        }

        // 2. LÓGICA DE IMAGEN
        try {
            if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
                String oldImageUrl = productoExistente.getImageUrl();
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    storageService.delete(oldImageUrl);
                }
                String newImageUrl = storageService.store(imagenArchivo);
                productoExistente.setImageUrl(newImageUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al subir o procesar la imagen durante la edición: " + e.getMessage(), e);
        }


        // 3. LÓGICA DE AUDITORÍA (Precio Histórico)
        boolean precioCambio = costoAnterior.compareTo(costoParaValidar) != 0;
        boolean costoCambio = precioAnterior.compareTo(precioParaValidar) != 0;
        boolean proveedorCambio = (proveedorAnteriorId == null && productoDTO.getProveedorId() != null) ||
                (proveedorAnteriorId != null && !proveedorAnteriorId.equals(productoDTO.getProveedorId()));


        if (precioCambio || costoCambio || proveedorCambio) {
            Optional<Usuario> usuarioOpt = usuarioService.findEntityById(userId);
            Usuario usuarioAuditor = usuarioOpt.orElseThrow(() -> new RuntimeException("Usuario auditor no encontrado."));

            PrecioHistoricoProducto historico = new PrecioHistoricoProducto();
            historico.setProducto(productoExistente);
            historico.setPrecioAnterior(precioAnterior);
            historico.setCostoAnterior(costoAnterior);
            historico.setStockAnterior(stockAnterior);

            historico.setPrecioNuevo(precioParaValidar);
            historico.setCostoNuevo(costoParaValidar);
            historico.setStockNuevo(stockAnterior);

            historico.setFechaCambio(LocalDateTime.now());
            historico.setUsuario(usuarioAuditor);

            precioHistoricoRepository.save(historico);
        }

        // 4. Actualizar Datos Finales
        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + productoDTO.getCategoriaId()));
        Proveedor nuevoProveedor = proveedorRepository.findById(productoDTO.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + productoDTO.getProveedorId()));


        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setPrecio(precioParaValidar);
        productoExistente.setCosto(costoParaValidar);
        productoExistente.setCategoria(categoria);
        productoExistente.setProveedor(nuevoProveedor);
        // NOTA: Se mantiene el stock (stockAnterior) porque este método NO debe actualizar stock.
        productoExistente.setStock(stockAnterior); 

        productoExistente.setFechaModificacion(LocalDateTime.now());

        Producto updatedProducto = productoRepository.save(productoExistente);
        return convertToDto(updatedProducto);
    }

    // --- ELIMINACIÓN ---
    @Override
    @Transactional
    public void deleteProducto(Long idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new RuntimeException("Producto no encontrado con id: " + idProducto);
        }

        Producto productoAEliminar = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));

        String imageUrl = productoAEliminar.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            storageService.delete(imageUrl);
        }

        precioHistoricoRepository.deleteByProductoId(idProducto);
        movimientoRepository.deleteByProductoId(idProducto);

        productoRepository.deleteById(idProducto);
    }



    @Override
    @Transactional
    public void agregarStock(Long productoId, Integer cantidad, String justificacion, Long usuarioId) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser un número positivo.");
        }

        MovimientoInventarioDTO movimiento = new MovimientoInventarioDTO();
        movimiento.setProductoId(productoId);
        movimiento.setCantidad(cantidad);
        movimiento.setTipoMovimiento(TipoMovimiento.AJUSTE_POSITIVO);
        movimiento.setJustificacion(justificacion);
        movimiento.setUsuarioId(usuarioId);

        movimientoInventarioService.registrarMovimiento(movimiento); 
    }

    @Override
    @Transactional
    public void eliminarStock(Long productoId, Integer cantidad, String justificacion, Long usuarioId) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a eliminar debe ser un número positivo.");
        }

        MovimientoInventarioDTO movimiento = new MovimientoInventarioDTO();
        movimiento.setProductoId(productoId);
        movimiento.setCantidad(-cantidad); // Cantidad negativa para la salida
        movimiento.setTipoMovimiento(TipoMovimiento.AJUSTE_NEGATIVO);
        movimiento.setJustificacion(justificacion);
        movimiento.setUsuarioId(usuarioId);

        movimientoInventarioService.registrarMovimiento(movimiento); 
    }

    // 🔥 IMPLEMENTACIÓN: Método "Puente" para obtener Historial de Movimientos
    @Override
    public List<MovimientoInventarioDTO> getHistorialMovimientosPorProducto(Long productoId) {
        // Delega la responsabilidad de obtener el historial al servicio de movimientos
        return movimientoInventarioService.getHistorialMovimientosPorProducto(productoId);
    }

    private ProveedorDTO convertToProveedorDto(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setContactoEmail(proveedor.getContactoEmail());
        dto.setContactoTelefono(proveedor.getContactoTelefono());
        dto.setFechaRegistro(proveedor.getFechaRegistro());
        dto.setActivo(proveedor.isActivo()); // Incluir el estado activo/bloqueado
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> findAllProveedores() {
        
        return proveedorRepository.findByActivoTrue()
                                .stream() 
                                // CORRECCIÓN FINAL: Llamar al nuevo mapeador de Proveedor
                                .map(this::convertToProveedorDto) 
                                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeStockPorAgotarse() {
        final int STOCK_MINIMO = 10; 
        
        // Si la cuenta de productos con stock <= 10 es mayor a cero, activamos la alerta
        return productoRepository.countByStockLessThanEqual(STOCK_MINIMO) > 0;
    }

    @Override
    @Transactional
    public void deactivateProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para desactivar. ID: " + id));

        // 🚨 Acción clave: Desactivar en lugar de eliminar
        producto.setActivo(false);
        producto.setFechaModificacion(LocalDateTime.now()); // Opcional, pero bueno para auditoría
        
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findAllActive() {
        return productoRepository.findAllByActivoTrue().stream() // Usamos el nuevo método del repositorio
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void bloquearProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para bloquear. ID: " + id));

        // 🚨 Acción clave: Bloquear el producto (estado temporal)
        producto.setBloqueado(true);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void desbloquearProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para desbloquear. ID: " + id));

        // 🚨 Acción clave: Desbloquear el producto (permitir su venta)
        producto.setBloqueado(false);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);
    }
    
    private Producto findProductoEntity(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }


    @Override
    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = findProductoEntity(id);
        producto.setActivo(Boolean.FALSE);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void activarProducto(Long id) {
        Producto producto = findProductoEntity(id);
        producto.setActivo(Boolean.TRUE);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);
    }
    
    @Override
    @Transactional
    public void cambiarEstadoBloqueo(Long id, boolean bloqueado) {
        Producto producto = findProductoEntity(id);
        
        // 🛑 Lógica de Seguridad: No se puede bloquear un producto inactivo.
        if (producto.getActivo() == Boolean.FALSE) {
             throw new RuntimeException("No se puede bloquear la venta de un producto que no está activo.");
        }
        
        producto.setBloqueado(bloqueado);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

}