package ferreteria.demo.service;

import ferreteria.demo.dto.RegistroClienteDTO;
import ferreteria.demo.dto.UsuarioCreateDTO;
import ferreteria.demo.dto.UsuarioDTO;
import ferreteria.demo.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<UsuarioDTO> findAllUsuarios();
    Long findIdByUsername(String username);

    // 1. Lectura de DTO (Uso principal en controladores)
    UsuarioDTO findById(Long idUsuario);

    // 🚀 CORRECCIÓN DEL CONFLICTO 🚀
    // Cambiamos el nombre del argumento para evitar Type Erasure y que el Impl compile.
    Optional<Usuario> findEntityById(Long usuarioId);

    // metodo para crear usuario con DTO de creacion
    UsuarioDTO crearUsuario(UsuarioCreateDTO createDTO);

    UsuarioDTO updateUsuario(Long idUsuario, UsuarioDTO usuarioDTO);

    void deleteUsuario(Long idUsuario);

    UsuarioDTO registrarCliente(RegistroClienteDTO dto);

    UsuarioDTO findByUsername(String username);
}