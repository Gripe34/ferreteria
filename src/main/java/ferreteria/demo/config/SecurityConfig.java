package ferreteria.demo.config;

import ferreteria.demo.security.CustomAuthenticationSuccessHandler;
import org.modelmapper.ModelMapper; // 🔥 NUEVA IMPORTACIÓN
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 🔥 SOLUCIÓN CRÍTICA: Declara ModelMapper aquí para asegurar su resolución
    // Esto previene la BeanCreationException que fallaba en la inyección de UsuarioServiceImpl
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize

                        // 1. URLs PÚBLICAS Y ESTATICAS
                        .requestMatchers(
                                "/",
                                "/tienda/**",
                                "/carrito/**",
                                "/registro",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/Imagenes/**",
                                "/web/**",
                                "/uploaded-products/**"
                        ).permitAll()

                        // 2. URLs de CLIENTE WEB (Protegidas)
                        .requestMatchers("/checkout/**", "/perfil/**").hasRole("CLIENTE")

                        // 3. URLs de EMPLEADOS/ADMI

                        // Gestión de Productos
                        .requestMatchers("/gestion-productos", "/gestion-productos/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                        .requestMatchers("/checkout/**", "/perfil/**", "/mi-cuenta/compras", "/mi-cuenta/detalle-compra/**").hasRole("CLIENTE")

                        // Rutas de Vendedor y Subprocesos (Incluye la nueva vista /vendedor/resumen-venta/ID)
                        .requestMatchers("/productos/stock", "/ventas/nueva", "/ventas/registrar").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers("/vendedor/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        // Otras rutas de administración (solo para ADMIN)
                        .requestMatchers("/admin/**", "/productos", "/productos/**").hasRole("ADMINISTRADOR")


                        // 4. Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                )

                // 5. Configurar el formulario de login
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());


        return http.build();
    }
}