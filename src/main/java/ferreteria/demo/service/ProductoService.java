package ferreteria.demo.service;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.dto.ProductoDTO;
import ferreteria.demo.dto.ProveedorDTO;
import ferreteria.demo.dto.PrecioHistoricoDTO;
import ferreteria.demo.entity.Producto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductoService {


    ProductoDTO crearProducto(ProductoDTO productoDTO, Long adminId, MultipartFile imagenArchivo);


    ProductoDTO updateProducto(Long idProducto, ProductoDTO productoDTO);

    void deleteProducto(Long idProducto);
    ProductoDTO findById(Long idProducto);
    Optional<Producto> findEntityById(Long idProducto);

    List<ProductoDTO> findAllProductos();
    List<ProductoDTO> findAllProductosConDetalle();

    List<ProductoDTO> findAllProductosParaVenta();
    List<ProductoDTO> findAllProductosActivos();


    ProductoDTO editarProducto(Long idProducto, ProductoDTO productoDTO, Long userId, MultipartFile imagenArchivo);

    List<PrecioHistoricoDTO> getHistorialPrecioPorProducto(Long productoId);

    void agregarStock(Long productoId, Integer cantidad, String justificacion, Long usuarioId);
    void eliminarStock(Long productoId, Integer cantidad, String justificacion, Long usuarioId);

    List<MovimientoInventarioDTO> getHistorialMovimientosPorProducto(Long productoId);


    List<ProveedorDTO> findAllProveedores();

    boolean existeStockPorAgotarse();

    List<ProductoDTO> findAllActive();

    void deactivateProducto(Long id);


    // 2. Para bloquear temporalmente un producto (cambia el campo 'bloqueado')
    void bloquearProducto(Long id);

    // 3. Para desbloquear un producto
    void desbloquearProducto(Long id);

    void desactivarProducto(Long id);

    void activarProducto(Long id);
    void cambiarEstadoBloqueo(Long id, boolean bloqueado);

}
