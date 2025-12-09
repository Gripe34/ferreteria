package ferreteria.demo.impl;

import ferreteria.demo.dto.CategoriaDTO;
import ferreteria.demo.entity.Categoria;
import ferreteria.demo.repository.CategoriaRepository;
import ferreteria.demo.service.CategoriaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ModelMapper modelMapper) {
        this.categoriaRepository = categoriaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CategoriaDTO> findAllCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaDTO findById(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + idCategoria));
        return modelMapper.map(categoria, CategoriaDTO.class);
    }

    @Override
    public CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO) {
        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        return modelMapper.map(savedCategoria, CategoriaDTO.class);
    }

    @Override
    public CategoriaDTO updateCategoria(Long idCategoria, CategoriaDTO categoriaDTO) {
        Categoria categoriaExistente = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + idCategoria));

        categoriaExistente.setNombre(categoriaDTO.getNombre());
        Categoria updatedCategoria = categoriaRepository.save(categoriaExistente);
        return modelMapper.map(updatedCategoria, CategoriaDTO.class);
    }

    @Override
    public void deleteCategoria(Long idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new RuntimeException("Categoría no encontrada con id: " + idCategoria);
        }
        categoriaRepository.deleteById(idCategoria);
    }
}
