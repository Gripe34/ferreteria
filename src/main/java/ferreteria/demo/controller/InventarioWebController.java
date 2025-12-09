package ferreteria.demo.controller;

// ... (imports)
import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.service.MovimientoInventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioWebController {

    private final MovimientoInventarioService movimientoService;

    public InventarioWebController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping("/trazabilidad")
    public String showTrazabilidad(Model model) {
        // Llamamos al servicio para obtener la lista de DTOs enriquecidos
        List<MovimientoInventarioDTO> movimientos = movimientoService.findAllMovimientosConDetalle();

        model.addAttribute("movimientos", movimientos);
        return "admin/trazabilidad";
    }
}