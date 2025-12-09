package ferreteria.demo.controller;

// Importaciones de Entidades y DTOs
import ferreteria.demo.dto.UsuarioDTO;
import ferreteria.demo.entity.Compra;
import ferreteria.demo.service.UsuarioService;
import ferreteria.demo.service.CompraService; 

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mi-cuenta")
public class ClienteWebController {

    private final CompraService compraService; // 🟢 Cambiado de VentaService
    private final UsuarioService usuarioService;

    // Constructor para inyección de dependencias
    public ClienteWebController(CompraService compraService, UsuarioService usuarioService) {
        this.compraService = compraService; // 🟢 Inyección del servicio de Compra
        this.usuarioService = usuarioService;
    }

    // ==========================================================
    // --- 1. ENDPOINT: PERFIL / MI CUENTA (/mi-cuenta/perfil) ---
    // ==========================================================

    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {
        String username = authentication.getName();

        UsuarioDTO cliente = usuarioService.findByUsername(username);

        if (cliente == null) {
            return "redirect:/login?error=profile_data_missing";
        }

        model.addAttribute("cliente", cliente);
        return "cliente/perfil";
    }

    // =====================================================================
    // --- 2. ENDPOINT: HISTORIAL DE COMPRAS (/mi-cuenta/compras) ---
    // =====================================================================

    @GetMapping("/compras")
    public String verHistorialDeCompras(Model model, Authentication authentication) {

        String username = authentication.getName();

        // 1. Obtener el usuario autenticado (asumiendo que UsuarioService lo devuelve)
        UsuarioDTO clienteDTO = usuarioService.findByUsername(username);

        // =================================================================
        // 🔥 PUNTO CRÍTICO DE DEPURACIÓN (Manejo de Errores)
        // =================================================================
        if (clienteDTO == null || clienteDTO.getId() == null) {
            // Logueamos el fallo si el DTO no tiene ID
            System.err.println("❌ ERROR DE SEGURIDAD/MAPEO: El usuario '" + username + "' se autenticó pero su ID no se pudo obtener del UsuarioDTO.");
            // Lanzamos una excepción para forzar una respuesta 403 o 500 y detener la ejecución.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Error de seguridad: El ID del usuario autenticado es nulo o no existe.");
        }

        // TEMPORAL: Logueamos el ID que se usará para la consulta
        System.out.println("✅ DEBUG: Consultando compras para el usuario ID: " + clienteDTO.getId());

        // 2. Obtener las compras asociadas a ese cliente
        // El servicio debe encontrar las compras usando el ID (Long)
        List<Compra> misCompras = compraService.encontrarComprasPorUsuario(clienteDTO.getId());

        // TEMPORAL: Logueamos si la lista tiene resultados
        System.out.println("✅ DEBUG: Compras encontradas: " + misCompras.size());

        model.addAttribute("misCompras", misCompras);


        model.addAttribute("compra", null);

        // Si el error persiste, prueba a eliminar el bloque th:unless del HTML.

        return "cliente/historial-compras";
    }

    // =====================================================================
    // 🟢 --- 3. ENDPOINT: DETALLE DE COMPRA (Para el Modal) ---
    // =====================================================================

    @GetMapping("/detalle-compra/{id}")
    public String cargarDetalleCompraFragmento(@PathVariable("id") Long compraId, Model model, Authentication authentication) {
        String username = authentication.getName();
        Long clienteId = usuarioService.findByUsername(username).getId();

        Optional<Compra> compraOpt = compraService.encontrarCompraPorIdYUsuarioConDetalles(compraId, clienteId);

        if (compraOpt.isEmpty()) {
            // Devuelve 404 si no se encuentra O si no pertenece al usuario. ¡Seguridad aplicada!
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada.");
        }

        model.addAttribute("compra", compraOpt.get());
        return "cliente/historial-compras :: detalleModalContent";
    }


}