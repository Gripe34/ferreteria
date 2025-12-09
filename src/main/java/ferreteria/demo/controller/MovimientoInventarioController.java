package ferreteria.demo.controller;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.service.MovimientoInventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    public ResponseEntity<MovimientoInventarioDTO> registrarMovimiento(@RequestBody MovimientoInventarioDTO dto) {
        // Tu lógica de negocio (validación y actualización de stock) ocurre dentro de este servicio.
        return new ResponseEntity<>(movimientoService.registrarMovimiento(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioDTO>> getAllMovimientos() {
        return new ResponseEntity<>(movimientoService.findAllMovimientos(), HttpStatus.OK);
    }


    // Usamos una URL descriptiva, como /api/movimientos/trazabilidad
    @GetMapping("/trazabilidad")
    public ResponseEntity<List<MovimientoInventarioDTO>> getAllMovimientosConDetalle() {

        List<MovimientoInventarioDTO> movimientosDetallados = movimientoService.findAllMovimientosConDetalle();
        return new ResponseEntity<>(movimientosDetallados, HttpStatus.OK);
    }

    // Puedes agregar @GetMapping("/{id}") si lo necesitas
}