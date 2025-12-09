package ferreteria.demo.controller;

import ferreteria.demo.config.Carrito;
import ferreteria.demo.dto.ItemCarritoDTO;
import ferreteria.demo.entity.Producto;
import ferreteria.demo.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final Carrito carrito;
    private final ProductoService productoService;

    // Redirección para el Catálogo (Seguir Comprando)
    private static final String CATALOGO_REDIRECT_URL = "redirect:/tienda";
    // Redirección para el Resumen del Carrito
    private static final String CARRITO_VIEW_REDIRECT = "redirect:/carrito";

    public CarritoController(Carrito carrito, ProductoService productoService) {
        this.carrito = carrito;
        this.productoService = productoService;
    }

    @GetMapping
    public String verCarrito(Model model) {
        model.addAttribute("carritoItems", carrito.getItems().values());
        model.addAttribute("totalCarrito", carrito.getTotalCarrito());
        model.addAttribute("cantidadTotal", carrito.getCantidadTotal());
        return "web/carrito";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam("productoId") Long productoId,
                                   @RequestParam("cantidad") int cantidad,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {

        if (cantidad <= 0) {
            redirectAttributes.addFlashAttribute("errorCarrito", "La cantidad debe ser mayor a cero.");
            return CATALOGO_REDIRECT_URL;
        }

        Optional<Producto> optionalProducto = productoService.findEntityById(productoId);

        if (optionalProducto.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCarrito", "Producto no encontrado.");
            return CATALOGO_REDIRECT_URL;
        }

        Producto producto = optionalProducto.get();

        int cantidadActualEnCarrito = carrito.getItems().getOrDefault(productoId, new ItemCarritoDTO()).getCantidad();
        if (producto.getStock() < (cantidadActualEnCarrito + cantidad)) {
            redirectAttributes.addFlashAttribute("errorCarrito", "Stock insuficiente para " + producto.getNombre() + ". Stock disponible: " + (producto.getStock() - cantidadActualEnCarrito));
            return CATALOGO_REDIRECT_URL;
        }

        ItemCarritoDTO item = new ItemCarritoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                cantidad
        );

        carrito.agregarItem(item);

        redirectAttributes.addFlashAttribute("mensajeCarritoExito", "¡Producto " + producto.getNombre() + " añadido al carrito exitosamente!");
        session.setAttribute("cartCount", carrito.getCantidadTotal());

        return CATALOGO_REDIRECT_URL; // Redirige al catálogo
    }


    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam("productoId") Long productoId,
                                     @RequestParam("cantidad") int cantidad,
                                     HttpSession session) {

        if (cantidad > 0) {
            carrito.actualizarItem(productoId, cantidad);
        } else {
            carrito.removerItem(productoId);
        }

        session.setAttribute("cartCount", carrito.getCantidadTotal());

        return CARRITO_VIEW_REDIRECT; // Redirige al resumen del carrito
    }


    @PostMapping("/remover/{productoId}")
    public String removerItem(@PathVariable Long productoId, HttpSession session) {
        carrito.removerItem(productoId);

        session.setAttribute("cartCount", carrito.getCantidadTotal());

        return CARRITO_VIEW_REDIRECT; // Redirige al resumen del carrito
    }


    @PostMapping("/finalizar-compra")
    public String finalizarCompra(HttpSession session, RedirectAttributes redirectAttributes) {

        // Simular lógica de registro de orden...

        carrito.limpiarCarrito();
        session.setAttribute("cartCount", 0);

        redirectAttributes.addFlashAttribute("mensajeExito", "¡Gracias! Tu orden ha sido registrada exitosamente.");

        return CATALOGO_REDIRECT_URL;
    }


    @GetMapping("/limpiar")
    public String limpiarCarrito(HttpSession session) {
        carrito.limpiarCarrito();

        session.setAttribute("cartCount", 0);

        return CATALOGO_REDIRECT_URL;
    }
}