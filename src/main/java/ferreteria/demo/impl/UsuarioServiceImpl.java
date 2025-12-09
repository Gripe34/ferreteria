package ferreteria.demo.impl;

import ferreteria.demo.dto.RegistroClienteDTO;
import ferreteria.demo.dto.UsuarioCreateDTO;
import ferreteria.demo.dto.UsuarioDTO;
import ferreteria.demo.entity.Rol;
import ferreteria.demo.entity.Usuario;
import ferreteria.demo.repository.UsuarioRepository;
import ferreteria.demo.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Usamos @Transactional de Spring

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // --- MÉTODOS DE LECTURA (DEBE COINCIDIR CON LA INTERFAZ) ---

    @Override
    public List<UsuarioDTO> findAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO findById(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    // 🚀 MÉTODO CORREGIDO: findEntityById(Long id) para devolver la Entidad 🚀
    // (Resuelve el error de Type Erasure y la necesidad de devolver la Entidad en ProductoServiceImpl)
    @Override
    public Optional<Usuario> findEntityById(Long id) {
        return usuarioRepository.findById(id);
    }

    // --- MÉTODOS DE CREACIÓN Y AUDITORÍA ---

    @Override
    @Transactional(readOnly = true)
    public Long findIdByUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.map(Usuario::getId).orElse(null);
    }

    @Override
    public UsuarioDTO crearUsuario(UsuarioCreateDTO createDTO) {
        Usuario usuario = modelMapper.map(createDTO, Usuario.class);

        //HASH DE LA CONTRASEÑA
        String hashedPassword = passwordEncoder.encode(createDTO.getPassword());
        usuario.setPassword(hashedPassword);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return modelMapper.map(savedUsuario, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO updateUsuario(Long idUsuario, UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));

        usuarioExistente.setUsername(usuarioDTO.getUsername());
        usuarioExistente.setRol(usuarioDTO.getRol());
        Usuario updatedUsuario = usuarioRepository.save(usuarioExistente);
        return modelMapper.map(updatedUsuario, UsuarioDTO.class);
    }

    @Override
    public void deleteUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("Usuario no encontrado con id: " + idUsuario);
        }
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    @Transactional
    public UsuarioDTO registrarCliente(RegistroClienteDTO dto) {

        if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());

        usuario.setRol(Rol.CLIENTE);

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(hashedPassword);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return modelMapper.map(savedUsuario, UsuarioDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .orElse(null); // Retorna null si no lo encuentra (aunque Spring Security debería garantizar que existe)
    }

}