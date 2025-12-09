package ferreteria.demo.repository;

import ferreteria.demo.entity.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// La interfaz debe extender JpaRepository, especificando la Entidad (DetalleCompra)
// y el tipo de dato de su clave primaria (Long).
@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    // Spring Data JPA ya proporciona los métodos save(), findById(), etc.

}