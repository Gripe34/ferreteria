package ferreteria.demo.controller;

import ferreteria.demo.dto.CategoriaRentabilidadDTO;
import ferreteria.demo.dto.GraficaVentaDTO;
import ferreteria.demo.dto.TendenciaVentaDTO;
import ferreteria.demo.dto.VentaDTO; // Necesario para la exportación
import ferreteria.demo.service.ReporteService;
import ferreteria.demo.service.ExcelExportService; // ✅ NUEVA IMPORTACIÓN
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Necesario para la respuesta binaria
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteApiController {

    private final ReporteService reporteService;
    private final ExcelExportService excelExportService; // ✅ NUEVA DEPENDENCIA

    // ✅ CONSTRUCTOR ACTUALIZADO: Inyectando ambos servicios
    public ReporteApiController(ReporteService reporteService, ExcelExportService excelExportService) {
        this.reporteService = reporteService;
        this.excelExportService = excelExportService;
    }

    // --- ENDPOINT 1: TOP PRODUCTOS ---

    /**
     * Endpoint 1: Ganancias y Cantidades por Producto (Gráficas 1 y 2)
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/ganancias-producto")
    public ResponseEntity<List<GraficaVentaDTO>> getTopProductos(
            @RequestParam(defaultValue = "10") int limite
    ) {
        try {
            List<GraficaVentaDTO> datos = reporteService.getTopProductosPorGanancia(limite);
            return new ResponseEntity<>(datos, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error al generar Top Ganancias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- ENDPOINT 2: RENTABILIDAD POR CATEGORÍA ---

    /**
     * Endpoint 2: Rentabilidad por Categoría (Gráfica 3)
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/rentabilidad-categoria")
    public ResponseEntity<List<CategoriaRentabilidadDTO>> getRentabilidadCategoria() {
        try {
            List<CategoriaRentabilidadDTO> datos = reporteService.getRentabilidadPorCategoria();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            System.err.println("Error al generar reporte de rentabilidad por categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- ENDPOINT 3: TENDENCIA DE VENTAS ---

    /**
     * Endpoint 3: Tendencia de Ventas Mensuales (Gráfica 4)
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/tendencia-ventas")
    public ResponseEntity<List<TendenciaVentaDTO>> getTendenciaVentas() {
        try {
            List<TendenciaVentaDTO> datos = reporteService.getTendenciaVentasMensuales();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            System.err.println("Error al generar reporte de tendencia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -----------------------------------------------------------------
    // --- ENDPOINT 4: EXPORTACIÓN A EXCEL (NUEVO) ---
    // -----------------------------------------------------------------

    /**
     * Endpoint para exportar el detalle de la tabla de ventas a un archivo Excel.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/exportar-ventas-excel")
    public ResponseEntity<byte[]> exportarVentas() {
        try {
            // 1. Obtener los datos de la tabla (usando el método que DEBES implementar)
            List<VentaDTO> ventas = reporteService.getAllVentasForExport();

            // 2. Generar el archivo Excel llamando al servicio de exportación
            byte[] excelBytes = excelExportService.exportVentasToExcel(ventas);

            // 3. Configurar las cabeceras HTTP para forzar la descarga
            HttpHeaders headers = new HttpHeaders();
            // Especifica que es un archivo binario
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Indica al navegador que descargue el archivo con el nombre especificado
            headers.setContentDispositionFormData("attachment", "reporte_ventas_" + System.currentTimeMillis() + ".xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error al generar el archivo Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}