package ferreteria.demo.repository;

import ferreteria.demo.entity.Proveedor;
import jakarta.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "false")})
    List<Proveedor> findByActivoTrue();
    List<Proveedor> findByActivoFalse();
    List<Proveedor> findAllByOrderByNombreAsc();

   
}