package ferreteria.demo.security;

import ferreteria.demo.entity.Usuario;
import ferreteria.demo.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos el usuario en nuestro repositorio.
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        // 2. Creamos una lista de "autoridades" (roles) para Spring Security.
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );

        // 3. Devolvemos un objeto "User" de Spring Security.
        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }
}
