package ferreteria.demo.repository;

import ferreteria.demo.entity.Usuario;
import ferreteria.demo.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByCliente(Usuario cliente);

    List<Venta> findByClienteId(Long clienteId);
}

