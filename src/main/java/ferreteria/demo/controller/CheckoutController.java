package ferreteria.demo.controller;

import ferreteria.demo.config.Carrito;
import ferreteria.demo.dto.DireccionDTO;
import ferreteria.demo.dto.PedidoDTO;
import ferreteria.demo.service.PedidoService;
import ferreteria.demo.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize; // Para seguridad
import org.springframework.security.core.Authentication; // Para obtener el usuario
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para mensajes flash

// Restringir el acceso solo a usuarios logueados (CLIENTE)
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final Carrito carrito;

    public CheckoutController(PedidoService pedidoService, UsuarioService usuarioService, Carrito carrito) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.carrito = carrito;
    }

    // Muestra la vista de dirección y resumen del pedido
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados (CLIENTE)
    @GetMapping
    public String showCheckout(Model model) {
        if (carrito.getItems().isEmpty()) {
            // No se puede hacer checkout con el carrito vacío
            return "redirect:/carrito";
        }

        model.addAttribute("carritoItems", carrito.getItems().values());
        model.addAttribute("totalPedido", carrito.getTotalCarrito());
        model.addAttribute("direccionDTO", new DireccionDTO()); // Objeto para el formulario

        return "web/checkout";
    }

    // Procesa el pedido, llama al servicio, consume stock y limpia el carrito
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/procesar")
    public String procesarPedido(@ModelAttribute DireccionDTO direccionDTO,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        try {
            if (carrito.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
                return "redirect:/carrito";
            }

            // 1. Obtener el ID del cliente logueado
            String username = authentication.getName();
            Long clienteId = usuarioService.findIdByUsername(username);

            if (clienteId == null) {
                throw new RuntimeException("Error: ID de cliente no encontrado en el sistema.");
            }

            // 2. Procesar el Checkout (Aquí se valida stock, se crea Pedido, Venta y Movimientos)
            PedidoDTO pedido = pedidoService.procesarCheckout(clienteId, carrito, direccionDTO);

            redirectAttributes.addFlashAttribute("mensajeExito", "¡Pedido #" + pedido.getId() + " registrado con éxito!");

            // 3. Redirigir a una página de confirmación
            return "redirect:/checkout/confirmacion";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout"; // Volver a la página de checkout con el error
        }
    }

    // Vista de confirmación simple (GET /checkout/confirmacion)
    @GetMapping("/confirmacion")
    public String showConfirmacion() {
        return "web/confirmacion";
    }
}