package ferreteria.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ferreteria.demo.entity.Compra; // Ajustado a 'entidades'
import ferreteria.demo.entity.Usuario; // Ajustado a 'entidades'
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // 🟢 Importación necesaria

import java.util.List;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByUsuario(Usuario usuario);



    @Query("SELECT c FROM Compra c " +
            "JOIN FETCH c.detalles d " +
            "JOIN FETCH d.producto p " +
            "WHERE c.id = :id")
    Optional<Compra> findByIdWithDetailsAndProducts(@Param("id") Long id);

    @Query("SELECT c FROM Compra c " +
            "JOIN FETCH c.detalles d " +
            "JOIN FETCH d.producto p " +
            "WHERE c.id = :compraId AND c.usuario = :usuario")
    Optional<Compra> findByIdAndUsuarioWithDetails(@Param("compraId") Long compraId, @Param("usuario") Usuario usuario);

    List<Compra> findByUsuario_Id(Long usuarioId);
}