package ferreteria.demo.controller;

import ferreteria.demo.dto.ProductoDTO;
import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.service.ProductoService;
import ferreteria.demo.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller; // ✅ USAMOS @Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Renombramos la clase para indicar que maneja vistas (Web/HTML)
@Controller
@RequestMapping("/vendedor") // Mapeo base para rutas de vista del vendedor
public class VentaWebController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    // UsuarioService no es necesario aquí, ya que el API Controller lo manejará en el POST.

    public VentaWebController(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }


    @GetMapping("/nueva-venta") // ✅ Ruta corregida para coincidir con el nombre de la plantilla
    public String mostrarFormularioDeVenta(Model model) {
        List<ProductoDTO> productosDisponibles = productoService.findAllProductosParaVenta();
        model.addAttribute("productos", productosDisponibles);
        return "vendedor/nueva-venta"; // Devuelve el nombre de la plantilla
    }

    /**
     * ✅ ENDPOINT DE LA VISTA DE RESUMEN (El que fallaba con 404)
     * Este endpoint ahora está garantizado de ser manejado por un @Controller.
     */
    @GetMapping("/resumen-venta/{id}")
    public String mostrarResumenVenta(@PathVariable("id") Long ventaId, Model model) {

        VentaDTO venta = ventaService.getVentaDetalleById(ventaId);

        if (venta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + ventaId);
        }

        model.addAttribute("venta", venta);

        return "vendedor/resumen_venta";
    }



    // ELIMINAMOS el @PostMapping("/registrar") de aquí, se mueve a la API.
}