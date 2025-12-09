package ferreteria.demo.controller;

import ferreteria.demo.service.ProductoService; // Importar el Servicio
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Necesario para pasar datos a la vista
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final ProductoService productoService;

    public AdminController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/index")
    public String showDashboard(Model model) { 
        
    
        boolean hayAlertaStock = productoService.existeStockPorAgotarse();
        
    
        model.addAttribute("alertaStock", hayAlertaStock);
        
        return "admin/index"; 
    }
}