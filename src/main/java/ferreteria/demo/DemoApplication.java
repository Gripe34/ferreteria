package ferreteria.demo;

import ferreteria.demo.entity.Rol;
import ferreteria.demo.entity.Usuario;
import ferreteria.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class    DemoApplication {  
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @Bean
    CommandLineRunner run(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Lógica para crear el usuario administrador inicial
            // 1. Verificamos si ya existe un usuario con el rol de ADMINISTRADOR
            if (usuarioRepository.findByRol(Rol.ADMINISTRADOR).isEmpty()) {

                // 2. Si no existe, creamos uno nuevo
                Usuario admin = new Usuario();
                admin.setUsername("admin");

                // 3.       Hasheamos la contraseña antes de guardarla
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(Rol.ADMINISTRADOR);

                // 4. Guardamos el nuevo administrador en la base de datos
                usuarioRepository.save(admin);

                // 5. Imprimimos un mensaje en la consola para saber las credenciales
                System.out.println(">>> Usuario administrador creado con éxito: admin / admin123 <<<");
            }
        };
    }

}




