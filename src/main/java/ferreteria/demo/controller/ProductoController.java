package ferreteria.demo.controller;

import ferreteria.demo.dto.ProductoDTO;
import ferreteria.demo.service.ProductoService;
import ferreteria.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Necesario para crearProducto

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public ProductoController(ProductoService productoService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    // Endpoint de creación de Producto (API REST)
    @PostMapping
    // Asumo que la firma de crearProducto es: crearProducto(ProductoDTO, Long, MultipartFile)
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO, Authentication authentication) {

        String username = authentication.getName();
        Long adminId = usuarioService.findIdByUsername(username);

        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Llamada a crearProducto con 'null' para el MultipartFile (típico de una llamada API JSON)
        ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO, adminId, null);

        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }



    // ... otros endpoints de API (GET, PUT, DELETE) ...
}