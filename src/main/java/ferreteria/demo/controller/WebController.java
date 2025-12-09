package ferreteria.demo.controller;

import ferreteria.demo.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// Cambiamos el RequestMapping de "/" a "/tienda"
@RequestMapping("/tienda")
public class WebController {

    private final ProductoService productoService;

    public WebController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping // Mapea la ruta raíz del controlador, es decir, /tienda
    public String verCatalogo(Model model) {
        model.addAttribute("productos", productoService.findAllProductosActivos());

        return "web/catalogo";
    }

}