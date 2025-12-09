package ferreteria.demo.service;

import ferreteria.demo.dto.CategoriaDTO;
import java.util.List;

public interface CategoriaService  {

    List<CategoriaDTO> findAllCategorias();

    CategoriaDTO findById(Long idCategoria);

    CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO);

    CategoriaDTO updateCategoria(Long idCategoria, CategoriaDTO categoriaDTO);

    void deleteCategoria(Long idCategoria);
}

