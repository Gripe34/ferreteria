package ferreteria.demo.controller;

import ferreteria.demo.dto.UsuarioCreateDTO;
import ferreteria.demo.dto.UsuarioDTO;
import ferreteria.demo.entity.Rol;
import ferreteria.demo.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioWebController {

    private final UsuarioService usuarioService;

    public UsuarioWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- LISTAR USUARIOS (Tabla Principal) ---
    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioDTO> listaDeUsuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", listaDeUsuarios);
        model.addAttribute("roles", List.of(Rol.ADMINISTRADOR, Rol.VENDEDOR, Rol.CLIENTE));

        return "admin/usuarios"; // Archivo: usuarios.html
    }


    // --- MOSTRAR FORMULARIO DE CREACIÓN ---
    @GetMapping("/nuevo")
    public String mostrarFormularioDeCreacion(Model model) {
        model.addAttribute("usuario", new UsuarioCreateDTO());
        model.addAttribute("roles", Rol.values());

        // 🚀 CORRECCIÓN: Usamos 'crearUsuario' (CamelCase, sin guion)
        return "admin/crear-usuario";
    }


    // --- PROCESAR CREACIÓN (POST) ---
    @PostMapping("/nuevo")
    public String crearUsuario(@ModelAttribute("usuario") UsuarioCreateDTO createDTO, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.crearUsuario(createDTO);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario " + createDTO.getUsername() + " creado con éxito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorCritico", "Error al crear usuario: " + e.getMessage());
            // Redirige al GET /nuevo (que usa el return 'crearUsuario')
            return "redirect:/admin/usuarios/nuevo";
        }

        return "redirect:/admin/usuarios";
    }


    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicion(@PathVariable Long id, Model model) {
        UsuarioDTO usuarioExistente = usuarioService.findById(id);
        model.addAttribute("usuario", usuarioExistente);
        model.addAttribute("roles", Rol.values());
        return "admin/editarUsuario";
    }

    @PostMapping("/editar/{id}")
    public String procesarFormularioDeEdicion(@PathVariable Long id, @ModelAttribute("usuario") UsuarioDTO usuarioDTO) {
        usuarioService.updateUsuario(id, usuarioDTO);
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return "redirect:/admin/usuarios";
    }
}