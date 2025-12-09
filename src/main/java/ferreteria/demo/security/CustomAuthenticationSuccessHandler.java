package ferreteria.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Obtenemos la URL de destino basada en el rol
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        // Redirigimos al usuario a la URL correspondiente
        response.sendRedirect(targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // La URL de destino por defecto para roles desconocidos
        String defaultUrl = "/tienda"; // Si el rol es desconocido, va a la tienda

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.contains("ADMINISTRADOR")) {
                return "/admin/index";
            } else if (role.contains("VENDEDOR")) {
                return "/vendedor/index";
            }


            else if (role.contains("CLIENTE")) {
                // Opción 1 (Simple): Siempre va a la tienda
                return "/tienda";

            }
        }

        // Si no se encuentra un rol válido (aunque Spring Security debería forzar al menos uno)
        // Redirige a una página genérica o lanza la excepción si no es CLIENTE/VENDEDOR/ADMIN.
        return defaultUrl;
    }
}