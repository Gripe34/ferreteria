package ferreteria.demo.repository;

import ferreteria.demo.entity.Producto; 
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProductoRepository extends JpaRepository<Producto, Long> { ;
   
    Long countByStockLessThanEqual(Integer stock);

    List<Producto> findAllByActivoTrue();

    List<Producto> findAll();
}
