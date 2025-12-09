package ferreteria.demo.controller;

import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    private final VentaService ventaService;

    public ReporteController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Muestra la página principal de reportes con una lista de todas las ventas.
     */
    @GetMapping
    public String mostrarPaginaDeReportes(Model model) {
        List<VentaDTO> listaDeVentas = ventaService.findAllVentas();
        model.addAttribute("ventas", listaDeVentas);
        return "admin/reportes";
    }
}
