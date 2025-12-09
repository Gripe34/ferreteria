package ferreteria.demo.repository;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    void deleteByProductoId(Long idProducto);


    @Query("SELECT m FROM MovimientoInventario m JOIN FETCH m.producto WHERE m.producto.id = :productoId ORDER BY m.fecha DESC")
    List<MovimientoInventario> findByProductoIdOrderByFechaDesc(@Param("productoId") Long productoId);

    // Si la anterior no funciona, prueba con la firma original (sin @Query):
    // List<MovimientoInventario> findByProductoIdOrderByFechaDesc(Long productoId);
    List<MovimientoInventario> findAllByOrderByFechaDesc();
}