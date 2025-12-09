package ferreteria.demo.repository;

import ferreteria.demo.entity.PrecioHistoricoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PrecioHistoricoProductoRepository extends JpaRepository<PrecioHistoricoProducto, Long> {

    List<PrecioHistoricoProducto> findByProductoIdOrderByFechaCambioDesc(Long productoId);


    void deleteByProductoId(Long idProducto);
}