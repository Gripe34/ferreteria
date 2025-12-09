package ferreteria.demo.impl;

import ferreteria.demo.dto.ProveedorDTO;
import ferreteria.demo.entity.Proveedor;
import ferreteria.demo.repository.ProveedorRepository;
import ferreteria.demo.service.ProveedorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    // --- UTILITIES (Mappers) ---

    // Conversión de Entity a DTO
    private  ProveedorDTO convertToDto(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setContactoEmail(proveedor.getContactoEmail());
        dto.setContactoTelefono(proveedor.getContactoTelefono());
        dto.setFechaRegistro(proveedor.getFechaRegistro());
        dto.setActivo(proveedor.isActivo());
        return dto;
    }

    // Conversión de DTO a Entity (Necesario para guardar)
    private Proveedor convertToEntity(ProveedorDTO dto) {
        Proveedor proveedor;
        if (dto.getId() != null) {
            // Busca si existe para actualización
            proveedor = proveedorRepository.findById(dto.getId()).orElse(new Proveedor());
        } else {
            proveedor = new Proveedor();
        }

        // Asignación de datos del DTO a la Entidad
        proveedor.setNombre(dto.getNombre());
        proveedor.setContactoEmail(dto.getContactoEmail());
        proveedor.setContactoTelefono(dto.getContactoTelefono());
        proveedor.setActivo(dto.isActivo());
        // La fecha de registro solo se establece si ya existe en el DTO (para actualizaciones)
        if (dto.getFechaRegistro() != null) {
            proveedor.setFechaRegistro(dto.getFechaRegistro());
        }
        return proveedor;
    }


    // --- IMPLEMENTACIONES DE LA INTERFAZ PROVEEDOR SERVICE ---

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> findAllProveedores() {
        return proveedorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ProveedorDTO save(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = convertToEntity(proveedorDTO);
        Proveedor savedProveedor = proveedorRepository.save(proveedor);
        return convertToDto(savedProveedor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // En este punto, solo eliminamos el proveedor. Si está asociado a un producto,
        // la BD tirará una excepción de clave foránea, lo cual es correcto.
        proveedorRepository.deleteById(id);
    }

    @Override
    @Transactional // Importante: Asegura que el cambio se guarde en la base de datos.
    public ProveedorDTO toggleBloqueo(Long proveedorId) {
        
        // 1. Buscar el proveedor por su ID. Si no se encuentra, lanza una excepción.
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + proveedorId));

        // 2. Aplicar la lógica de negocio: Alternar el estado.
        // Si estaba activo (true), se vuelve inactivo (false), y viceversa.
        proveedor.setActivo(!proveedor.isActivo());
        
        // 3. Guardar el cambio en la base de datos.
        Proveedor savedProveedor = proveedorRepository.save(proveedor);
        
        // 4. Retornar el DTO actualizado (necesitarás el método de mapeo).
        // Nota: Asumo que tienes un método convertToDto(Proveedor entity)
        return convertToDto(savedProveedor); 
    }

   @Override
public List<ProveedorDTO> findAllProveedoresActivos() {
    return proveedorRepository.findByActivoTrue()
                              .stream()
                              // 🛑 LLAMADA CORRECTA: Usamos el método que SÍ existe en esta clase
                              .map(this::convertToDto) 
                              .collect(Collectors.toList());
}

}