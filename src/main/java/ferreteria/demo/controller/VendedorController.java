package ferreteria.demo.controller;

import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.service.ProductoService;
import ferreteria.demo.service.VentaService;
import ferreteria.demo.dto.ProductoDTO;
import ferreteria.demo.dto.VentasDashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/vendedor")
public class VendedorController {

    private final ProductoService productoService;
    private final VentaService ventaService; // 🔥 Declarar el servicio de ventas

    // Constructor para inyección de dependencias
    public VendedorController(ProductoService productoService, VentaService ventaService) {
        this.productoService = productoService;
        this.ventaService = ventaService; // 🔥 Inicializar el servicio
    }

    @GetMapping("/index")
    public String showDashboard() {
        return "vendedor/index";
    }

    // --- Nuevo Endpoint: Consulta de Stock (vendedor/stock.html) ---


    @GetMapping("/dashboard-ventas")
    public String mostrarDashboardVentas(Model model) {
        // 1. Obtener el DTO con las ventas ya clasificadas por el servicio
        VentasDashboardDTO dashboard = ventaService.getVentasClasificadas();

        // 2. Enviar el DTO al modelo (y a la vista)
        model.addAttribute("dashboard", dashboard);

        // 3. Retornar el nombre de la vista (la crearemos a continuación)
        return "vendedor/dashboard-ventas";
    }

    @GetMapping("/detalle-venta/{id}")
    public String mostrarDetalleVenta(@PathVariable Long id, Model model) {

        // 1. Obtener el DTO completo de la venta (con detalles, vendedor, etc.)
        VentaDTO venta = ventaService.findById(id);

        model.addAttribute("venta", venta);

        // 2. Retornar el fragmento HTML del modal
        return "vendedor/modal-detalle-venta";
    }

    @GetMapping("/consultar-stock")
    public String consultarStock(Model model) {
        model.addAttribute("productos", productoService.findAllProductosConDetalle());
        return "vendedor/stock";
    }

    @GetMapping("/reporte-ventas") // Este es el endpoint que creamos
    public String mostrarReporteVentas(Model model) {

        List<VentaDTO> todasLasVentas = ventaService.findAllVentasConDetalle();

        model.addAttribute("ventas", todasLasVentas); // Usaremos 'ventas' como nombre de variable

        return "vendedor/reporte-ventas"; // Retornar la nueva plantilla
    }

    @GetMapping("/reporte-ventas-general") // Usamos una ruta nueva para el Admin
    public String mostrarReporteGeneral(Model model) {
        List<VentaDTO> todasLasVentas = ventaService.findAllVentasConDetalle();

        model.addAttribute("ventas", todasLasVentas);

        return "vendedor/reporte-ventas";
    }



}