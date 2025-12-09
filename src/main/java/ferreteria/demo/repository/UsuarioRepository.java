package ferreteria.demo.repository;

import ferreteria.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import ferreteria.demo.entity.Rol;

import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByRol(Rol rol);
    Optional<Usuario> findByUsername(String username);
}

