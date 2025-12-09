package ferreteria.demo.controller;

import ferreteria.demo.dto.RegistroClienteDTO;
import ferreteria.demo.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult; // Para manejar errores de validación
import jakarta.validation.Valid; // Para activar la validación en el DTO

@Controller
@RequestMapping("/")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Muestra la vista de LOGIN (usualmente mapeada por Spring Security)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Suponiendo que tienes esta vista
    }

    // Muestra el formulario de REGISTRO (GET /registro)
    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registroClienteDTO", new RegistroClienteDTO());
        return "web/registro";
    }

    // Procesa el formulario de REGISTRO (POST /registro)
    @PostMapping("/registro")
    public String registerUser(@Valid @ModelAttribute("registroClienteDTO") RegistroClienteDTO registroDTO,
                               BindingResult result,
                               Model model) {

        // 1. Manejo de errores de validación (@NotBlank, @Email, etc.)
        if (result.hasErrors()) {
            return "web/registro"; // Vuelve al formulario si hay errores
        }

        try {
            // 2. Llama al servicio para registrar (con Rol.CLIENTE)
            usuarioService.registrarCliente(registroDTO);

            // 3. Redirige a login o a checkout
            model.addAttribute("mensajeExito", "¡Registro exitoso! Por favor, inicia sesión.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            // 4. Manejo de errores de negocio (ej. usuario ya existe)
            model.addAttribute("errorRegistro", e.getMessage());
            return "web/registro";
        }
    }
}