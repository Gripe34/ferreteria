package ferreteria.demo.controller;

import ferreteria.demo.dto.ProveedorDTO;
import ferreteria.demo.service.ProveedorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/gestion-proveedores")
@PreAuthorize("hasRole('ADMINISTRADOR')") // Solo el administrador puede gestionar proveedores
public class ProveedorWebController {

    private final ProveedorService proveedorService;

    public ProveedorWebController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // --- 1. LISTAR PROVEEDORES ---
    @GetMapping
    public String listarProveedores(Model model) {

        List<ProveedorDTO> proveedores = proveedorService.findAllProveedores();

        model.addAttribute("proveedores", proveedores);
        model.addAttribute("nuevoProveedor", new ProveedorDTO());

        // Retornamos la vista final (que redirigiremos a la carpeta proveedor/ para consistencia)
        return "proveedor/gestion-proveedores";
    }

    // --- 2. MOSTRAR FORMULARIO DE CREACIÓN (GET /gestion-proveedores/nuevo) ---
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {

        model.addAttribute("proveedorDTO", new ProveedorDTO());

        // Retorna la ruta del formulario de creación/edición
        return "proveedor/crearProveedor";
    }

    // --- 3. PROCESAR CREACIÓN/GUARDADO (POST /gestion-proveedores/guardar) ---
    @PostMapping("/guardar")
    public String guardarProveedor(@ModelAttribute("proveedorDTO") ProveedorDTO proveedorDTO, RedirectAttributes redirectAttributes) {

        try {
            // Asumo que el ProveedorService tiene un método save que maneja la Entidad
            // Necesitarás implementar save(ProveedorDTO dto) en tu ProveedorService
            proveedorService.save(proveedorDTO);

            redirectAttributes.addFlashAttribute("mensajeExito", "Proveedor '" + proveedorDTO.getNombre() + "' registrado con éxito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorCritico", "Error al registrar proveedor: " + e.getMessage());
            // Si hay error, volvemos a mostrar el formulario de creación/edición
            return "redirect:/gestion-proveedores/nuevo";
        }

        // Redirigir al listado para ver el nuevo proveedor
        return "redirect:/gestion-proveedores";
    }

    // --- 4. PLACEHOLDER: ELIMINAR PROVEEDOR ---
    // NOTA: Si un proveedor tiene productos asociados, la BD tirará un error de clave foránea.
    // Necesitas eliminar o reasignar esos productos antes de eliminar el proveedor.

    @PostMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            proveedorService.delete(id); // Asumo que este método existe
            redirectAttributes.addFlashAttribute("mensajeExito", "Proveedor eliminado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCritico", "No se pudo eliminar el proveedor. Razón: " + e.getMessage());
        }
        return "redirect:/gestion-proveedores";
    }

    @PostMapping("/bloquear/{id}")
public String toggleBloqueoProveedor(
        @PathVariable Long id, 
        RedirectAttributes redirectAttributes) {
    
    try {
        // Llama a la lógica del servicio que cambia el estado
        ProveedorDTO proveedorActualizado = proveedorService.toggleBloqueo(id);
        
        // Define el mensaje de éxito basado en el nuevo estado
        String estado = proveedorActualizado.isActivo() ? "activado" : "bloqueado";
        String mensaje = "El proveedor '" + proveedorActualizado.getNombre() + 
                         "' ha sido " + estado + " exitosamente.";
        
        redirectAttributes.addFlashAttribute("mensajeExito", mensaje);
        
    } catch (RuntimeException e) {
        // Manejo de errores (ej. Proveedor no encontrado)
        redirectAttributes.addFlashAttribute("errorCritico", 
                                             "Error al cambiar el estado del proveedor: " + e.getMessage());
    }
    
    // Redirige de vuelta a la lista principal de proveedores
    return "redirect:/gestion-proveedores"; 
}



}