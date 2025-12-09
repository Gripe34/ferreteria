package ferreteria.demo.impl;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.entity.MovimientoInventario;
import ferreteria.demo.entity.Producto;
import ferreteria.demo.entity.TipoMovimiento;
import ferreteria.demo.repository.MovimientoInventarioRepository;
import ferreteria.demo.repository.ProductoRepository;
import ferreteria.demo.repository.UsuarioRepository;
import ferreteria.demo.service.MovimientoInventarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ferreteria.demo.service.ProductoService;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ferreteria.demo.service.ProductoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final ProductoService productoService;

    public MovimientoInventarioServiceImpl(
            MovimientoInventarioRepository movimientoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository,
            ModelMapper modelMapper,
            @Lazy ProductoService productoService) {

        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.productoService = productoService;
    }

    // --- LÓGICA DE ESCRITURA: REGISTRAR Y ACTUALIZAR STOCK ---

    @Override
@Transactional // La anotación crucial para garantizar que si falla el guardado del movimiento o del producto, ambos se deshacen (rollback).
public MovimientoInventarioDTO registrarMovimiento(MovimientoInventarioDTO dto) {


    Producto producto = productoRepository.findById(dto.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + dto.getProductoId()));

    
    final Integer stockAntes = producto.getStock();

    Integer cambioNeto = dto.getCantidad(); 

    Integer nuevoStock = stockAntes + cambioNeto; 
   
    if (nuevoStock < 0) {
        throw new RuntimeException("Error de Stock: La cantidad a mover dejaría el stock de " + producto.getNombre() + " en valor negativo.");
    }
    producto.setStock(nuevoStock);
    
    productoRepository.save(producto);

    MovimientoInventario movimiento = new MovimientoInventario();
    movimiento.setProducto(producto);
    movimiento.setTipoMovimiento(dto.getTipoMovimiento());
    movimiento.setCantidad(cambioNeto);
    movimiento.setFecha(LocalDateTime.now());
    
    movimiento.setStockAnterior(stockAntes); 
    
    // Seteo de campos adicionales del movimiento.
    movimiento.setJustificacion(dto.getJustificacion());
    movimiento.setDocumentoOrigenId(dto.getDocumentoOrigenId());
    movimiento.setUsuarioId(dto.getUsuarioId());

    MovimientoInventario savedMovimiento = movimientoRepository.save(movimiento);

    return convertToDto(savedMovimiento);
}


    @Override
    public List<MovimientoInventarioDTO> findAllMovimientos() {
        return movimientoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoInventarioDTO findById(Long id) {
        MovimientoInventario movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento de Inventario no encontrado con id: " + id));
        return convertToDto(movimiento);
    }

    @Override
public List<MovimientoInventarioDTO> findAllMovimientosConDetalle() {

    
    List<MovimientoInventario> movimientos = movimientoRepository.findAllByOrderByFechaDesc();

    return movimientos.stream()
        .map(movimiento -> {

            MovimientoInventarioDTO dto = convertToDto(movimiento);

            // --- Lógica de Enriquecimiento (Mantenemos tu código) ---
            
            // Enriquecimiento con Username
            if (movimiento.getUsuarioId() != null) {
                usuarioRepository.findById(movimiento.getUsuarioId()).ifPresent(usuario -> {
                    dto.setUsuarioUsername(usuario.getUsername());
                });
            } else {
                dto.setUsuarioUsername("Sistema/Desconocido");
            }
            
            // Enriquecimiento con Nombre del Producto
            if (movimiento.getProducto() != null) {
                 productoRepository.findById(movimiento.getProducto().getId()).ifPresent(producto -> {
                    dto.setProductoNombre(producto.getNombre());
                 });
            } else {
                dto.setProductoNombre("Producto Eliminado");
            }
            
            // Enriquecimiento con el stock final del movimiento (asumiendo que el campo existe en la entidad)
            // Si tu DTO tiene un setter para stockActual, úsalo aquí.
            // dto.setStockActual(movimiento.getStockActual()); 

            // 2. ADAPTACIÓN DE SIGNO: Si la cantidad es negativa, se convierte a valor absoluto
            // (Esto es necesario para que la columna 'Cambio' en la vista muestre un valor positivo)
            if (dto.getCantidad() < 0) {
                 dto.setCantidad(dto.getCantidad() * -1);
            }

            return dto;
        })
        .collect(Collectors.toList());
}




    @Override
@Transactional(readOnly = true)
public List<MovimientoInventarioDTO> getHistorialMovimientosPorProducto(Long productoId) {


    List<MovimientoInventario> movimientos = movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);

    // 2. Mapear y enriquecer cada DTO
    return movimientos.stream().map(movimiento -> {

        // 2.1. Mapeo inicial (Asumimos que convertToDto está bien)
        MovimientoInventarioDTO dto = convertToDto(movimiento);

        // a. Mapear el nombre del producto (ya está bien si el JOIN FETCH funciona)
        if (movimiento.getProducto() != null) {
            dto.setProductoNombre(movimiento.getProducto().getNombre());
        } else {
            // Este caso solo ocurre si el producto fue borrado después del movimiento
            dto.setProductoNombre("Producto Eliminado");
        }

        // -----------------------------------------------------------
        // b. Enriquecimiento con Username (SECCIÓN BLINDADA)
        // -----------------------------------------------------------
        
        Long userId = movimiento.getUsuarioId();
        
        // Verificamos explícitamente que el ID de usuario no sea nulo antes de buscar.
        if (userId != null) { 
            // Acceso seguro a Usuario, usando el objeto cargado o buscando por ID
            usuarioRepository.findById(userId).ifPresent(usuario -> {
                dto.setUsuarioUsername(usuario.getUsername());
            });
        }
        
        // Si no se encontró el usuario (o userId era nulo), asignamos un valor por defecto.
        if (dto.getUsuarioUsername() == null) {
            dto.setUsuarioUsername("Sistema/Desconocido");
        }
        // -----------------------------------------------------------


        // c. Corregir el signo de la cantidad para la vista (mostrar valor absoluto)
        // Nota: Esto solo cambia el DTO para la vista; la DB sigue guardando el signo real.
        if (dto.getCantidad() < 0) {
            dto.setCantidad(dto.getCantidad() * -1);
        }

        return dto;

    }).collect(Collectors.toList());
}

    // --- MAPPERS ---

    private MovimientoInventarioDTO convertToDto(MovimientoInventario movimiento) {
        MovimientoInventarioDTO dto = modelMapper.map(movimiento, MovimientoInventarioDTO.class);

        // Mapear campos de relaciones
        dto.setProductoId(movimiento.getProducto().getId());
        dto.setDocumentoOrigenId(movimiento.getDocumentoOrigenId());
        dto.setUsuarioId(movimiento.getUsuarioId());

        // Mapear la justificación
        dto.setJustificacion(movimiento.getJustificacion());

        return dto;
    }
}