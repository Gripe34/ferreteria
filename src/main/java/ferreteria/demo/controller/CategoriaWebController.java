package ferreteria.demo.controller;

import ferreteria.demo.dto.CategoriaDTO;
import ferreteria.demo.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaWebController {

    private final CategoriaService categoriaService;

    public CategoriaWebController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }


    @GetMapping
    public String listarCategorias(Model model) {
        List<CategoriaDTO> listaDeCategorias = categoriaService.findAllCategorias();
        model.addAttribute("categorias", listaDeCategorias);
        return "admin/categorias";
    }


    @GetMapping("/nuevo")
    public String mostrarFormularioDeCreacion(Model model) {
        model.addAttribute("categoria", new CategoriaDTO());
        return "admin/crear-categoria";
    }

    @PostMapping("/nuevo")
    public String procesarFormularioDeCreacion(@ModelAttribute("categoria") CategoriaDTO categoriaDTO) {
        categoriaService.crearCategoria(categoriaDTO);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicion(@PathVariable Long id, Model model) {
        CategoriaDTO categoriaExistente = categoriaService.findById(id);
        model.addAttribute("categoria", categoriaExistente);
        return "admin/editar-categoria";
    }


    @PostMapping("/editar/{id}")
    public String procesarFormularioDeEdicion(@PathVariable Long id, @ModelAttribute("categoria") CategoriaDTO categoriaDTO) {
        categoriaService.updateCategoria(id, categoriaDTO);
        return "redirect:/admin/categorias";
    }


    @PostMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        return "redirect:/admin/categorias";
    }
}
