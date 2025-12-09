package ferreteria.demo.controller;

import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.service.FacturaService;
import ferreteria.demo.service.UsuarioService;
import ferreteria.demo.service.VentaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // ✅ SOLO UNA ANOTACIÓN REST
@RequestMapping("/api/ventas") // Mapeo base para la API
public class VentaApiController {

    private final VentaService ventaService;
    private final UsuarioService usuarioService;
    private final FacturaService facturaService;

    public VentaApiController(VentaService ventaService, UsuarioService usuarioService, FacturaService facturaService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
        this.facturaService = facturaService;
    }

    // --- 1. POST: Registrar Venta (JSON) ---
    @PostMapping
    public ResponseEntity<VentaDTO> registrarVenta(@RequestBody VentaDTO ventaDTO, Authentication authentication) {

        try {
            String username = authentication.getName();
            Long vendedorId = usuarioService.findIdByUsername(username);

            if (vendedorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // PROCESAR LA VENTA: Devuelve el DTO con el ID generado.
            VentaDTO nuevaVenta = ventaService.crearVenta(ventaDTO, vendedorId);

            // RESPUESTA EXITOSA (Frontend usa este ID para redirigir a la vista de resumen)
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- 2. GET: Generar Factura PDF ---
    @GetMapping("/factura/{ventaId}")
    public ResponseEntity<byte[]> getFacturaPDF(@PathVariable Long ventaId) {
        try {
            byte[] pdfBytes = facturaService.generarFacturaPdf(ventaId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "factura_" + ventaId + ".pdf";
            headers.setContentDispositionFormData("inline", filename);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF.".getBytes());
        }
    }

    // --- 3. GET: Listar Todas las Ventas ---
    @GetMapping
    public ResponseEntity<List<VentaDTO>> getAllVentas() {
        List<VentaDTO> ventas = ventaService.findAllVentas();
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }

    // --- 4. GET: Venta por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> getVentaById(@PathVariable Long id) {
        VentaDTO venta = ventaService.findById(id);
        return new ResponseEntity<>(venta, HttpStatus.OK);
    }
}