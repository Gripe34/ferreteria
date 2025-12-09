package ferreteria.demo.impl;

import ferreteria.demo.config.Carrito;
import ferreteria.demo.dto.DireccionDTO;
import ferreteria.demo.dto.ItemCarritoDTO;
import ferreteria.demo.dto.PedidoDTO;
import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.entity.DetalleCompra; // Importación necesaria para el historial
import ferreteria.demo.entity.DetallePedido;
import ferreteria.demo.entity.Pedido;
import ferreteria.demo.entity.Usuario;
import ferreteria.demo.repository.PedidoRepository;
import ferreteria.demo.repository.ProductoRepository;
import ferreteria.demo.repository.UsuarioRepository;
import ferreteria.demo.repository.DetalleCompraRepository; // <--- NUEVO: Inyectar para guardar detalles
import ferreteria.demo.service.CompraService; // <--- NUEVO: Servicio de Compra
import ferreteria.demo.service.PedidoService;
import ferreteria.demo.service.VentaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final VentaService ventaService;
    private final ModelMapper modelMapper;

    private final CompraService compraService; // <--- Nuevo Campo
    private final DetalleCompraRepository detalleCompraRepository; // <--- Nuevo Campo

    // Constructor inyectando las dependencias
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            VentaService ventaService,
            ModelMapper modelMapper,
            CompraService compraService, // <--- Nuevo Parámetro
            DetalleCompraRepository detalleCompraRepository) { // <--- Nuevo Parámetro

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.ventaService = ventaService;
        this.modelMapper = modelMapper;
        this.compraService = compraService; // <--- Asignación
        this.detalleCompraRepository = detalleCompraRepository; // <--- Asignación
    }

    @Override
    @Transactional
    public PedidoDTO procesarCheckout(Long clienteId, Carrito carrito, DireccionDTO direccion) {

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito de compras está vacío.");
        }

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el pedido."));

        Long sistemaUserId = 1L; // Asumiendo que el ID 1 pertenece al Administrador del sistema

        // 1. Revalidación CRÍTICA de Stock y Cálculo
        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (ItemCarritoDTO item : carrito.getItems().values()) {
            productoRepository.findById(item.getProductoId()).ifPresentOrElse(producto -> {
                if (producto.getStock() < item.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
                }
            }, () -> {
                throw new RuntimeException("Producto ID " + item.getProductoId() + " no existe.");
            });
            totalCalculado = totalCalculado.add(item.getSubtotal());
        }

        // 2. Creación de la Entidad Pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTotal(totalCalculado);
        pedido.setEstado("PENDIENTE_PAGO");
        pedido.setDireccionEnvio(direccion.getCallePrincipal());
        pedido.setCiudad(direccion.getCiudad());
        pedido.setCodigoPostal(direccion.getCodigoPostal());

        // Guardamos el encabezado del Pedido
        Pedido savedPedido = pedidoRepository.save(pedido);

        // 3. Crear DetallePedido
        for (ItemCarritoDTO item : carrito.getItems().values()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(savedPedido);
            detalle.setProducto(productoRepository.getReferenceById(item.getProductoId()));
            detalle.setCantidad(item.getCantidad());
            // Nota: Aquí se usa el precio del DTO, debe coincidir con el tipo Double/BigDecimal de tu entidad.
            detalle.setPrecioUnitarioVenta(item.getPrecioUnitario());
            savedPedido.getDetalles().add(detalle);
        }

        // 4. Consumir el Stock (Llamar al Servicio de Ventas para Trazabilidad)
        VentaDTO ventaRegistrada = ventaService.crearVentaInternaDesdePedido(savedPedido, sistemaUserId);

        // 5. Vincular la Venta al Pedido y Actualizar Estado
        savedPedido.setCodigoVentaId(ventaRegistrada.getId());
        savedPedido.setEstado("PAGADO/PROCESADO");

        // =======================================================================
        // 🛑 CONEXIÓN AL HISTORIAL DE COMPRAS (ENTIDAD COMPRA)
        // =======================================================================

        // A. Convertir Ítems del Carrito a DetalleCompra (Estructura necesaria para CompraService)
        List<DetalleCompra> detallesCompra = new ArrayList<>();

        for (ItemCarritoDTO item : carrito.getItems().values()) {
            DetalleCompra dc = new DetalleCompra();

            // Asignación de valores (Asumiendo que precioUnitario en DetalleCompra es Double)
            dc.setCantidad(item.getCantidad());
            // Si item.getPrecioUnitario() es BigDecimal, debes convertirlo:
            dc.setPrecioUnitario(item.getPrecioUnitario().doubleValue());
            dc.setProducto(productoRepository.getReferenceById(item.getProductoId()));

            detallesCompra.add(dc);
        }

        // B. Llamar al servicio que guarda el registro en la tabla 'compras' del historial
        // Esto guarda la Compra (asociada al Cliente) y guarda los DetalleCompra
        compraService.procesarNuevaCompra(cliente, detallesCompra);


        // 6. Limpiar el carrito de sesión
        carrito.limpiarCarrito();

        return modelMapper.map(savedPedido, PedidoDTO.class);
    }

    // --- MÉTODOS DE LECTURA ---

    @Override
    public PedidoDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return modelMapper.map(pedido, PedidoDTO.class);
    }
}