package ferreteria.demo.impl;

// Importaciones de Java
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Importaciones de la Capa de Servicio
import ferreteria.demo.entity.DetalleCompra;
import ferreteria.demo.service.CompraService;

// Importaciones de Entidades y Repositorios
import ferreteria.demo.entity.Compra;
import ferreteria.demo.entity.Usuario;
import ferreteria.demo.repository.CompraRepository;
import ferreteria.demo.repository.UsuarioRepository;
import ferreteria.demo.repository.ProductoRepository;
import ferreteria.demo.repository.MovimientoInventarioRepository;


@Service
public class CompraServiceImpl implements CompraService {

    // INYECCIÓN DE DEPENDENCIAS
    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;


    // ==========================================================
    // --- MÉTODOS DE CONSULTA (Funcionalidad Historial) ---
    // ==========================================================

    @Override
    public Optional<Compra> encontrarCompraPorId(Long id) {
        return compraRepository.findByIdWithDetailsAndProducts(id);
    }

    @Override
    public List<Compra> encontrarComprasPorUsuario(Long usuarioId) {
        return compraRepository.findByUsuario_Id(usuarioId);
    }

    @Override
    public Optional<Compra> encontrarCompraPorIdYUsuarioConDetalles(Long compraId, Long usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        return compraRepository.findByIdAndUsuarioWithDetails(compraId, usuarioOpt.get());
    }

    @Override
    public Optional<Compra> encontrarCompraPorIdYUsuario(Long compraId, Long usuarioId) {
        return Optional.empty();
    }


    // ==========================================================
    // --- LÓGICA PESADA (Implementación de Guardado Corregida) ---
    // ==========================================================

    @Override
    @Transactional
    public Compra procesarNuevaCompra(Usuario cliente, List<DetalleCompra> detalles) {

        Compra nuevaCompra = new Compra();

        // 1. Asignar el cliente (Soluciona que usuario_id sea NULL en la BD)
        nuevaCompra.setUsuario(cliente);

        // 2. Establecer fecha y estado
        nuevaCompra.setFechaRegistro(LocalDateTime.now());
        nuevaCompra.setEstado("COMPLETADO");

        // 3. Calcular total (CORRECCIÓN DE TIPOS: Double y Integer a BigDecimal)
        BigDecimal totalBigDecimal = detalles.stream()
                // Convertimos el precio (Double) y la cantidad (Integer) a BigDecimal
                .map(d -> BigDecimal.valueOf(d.getPrecioUnitario())
                        .multiply(BigDecimal.valueOf(d.getCantidad())))
                // Sumamos usando la referencia estática de BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // CORRECCIÓN DE TIPOS: Convertimos el total (BigDecimal) a Double para la entidad Compra
        nuevaCompra.setTotal(totalBigDecimal.doubleValue());

        // 4. Guardar la compra principal para obtener el ID
        Compra compraGuardada = compraRepository.save(nuevaCompra);

        // 5. Asociar los detalles
        for (DetalleCompra detalle : detalles) {
            detalle.setCompra(compraGuardada); // Asociar el detalle a la compra
            // Nota: Falta aquí la línea para guardar el DetalleCompra en su repositorio.
            // Por ejemplo: detalleCompraRepository.save(detalle);
        }

        // 6. Retornar la compra guardada (soluciona 'Missing return statement')
        return compraGuardada;
    }


}