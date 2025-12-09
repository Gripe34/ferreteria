package ferreteria.demo.controller;

import ferreteria.demo.dto.CategoriaDTO;
import ferreteria.demo.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias") // Ruta base para todas las operaciones de categorías
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Endpoint para obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getAllCategorias() {
        List<CategoriaDTO> categorias = categoriaService.findAllCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    // Endpoint para obtener una categoría por su ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> getCategoriaById(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.findById(id);
        return new ResponseEntity<>(categoria, HttpStatus.OK);
    }

    // Endpoint para crear una nueva categoría
    @PostMapping
    public ResponseEntity<CategoriaDTO> createCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO nuevaCategoria = categoriaService.crearCategoria(categoriaDTO);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED); // 201 Created
    }

    // Endpoint para actualizar una categoría existente
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO categoriaActualizada = categoriaService.updateCategoria(id, categoriaDTO);
        return new ResponseEntity<>(categoriaActualizada, HttpStatus.OK);
    }

    // Endpoint para eliminar una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}
