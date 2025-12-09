package ferreteria.demo.service;

import ferreteria.demo.dto.ProveedorDTO;
import java.util.List;

public interface ProveedorService {

    /** Devuelve una lista de todos los proveedores disponibles. */
    List<ProveedorDTO> findAllProveedores();

    ProveedorDTO save(ProveedorDTO proveedorDTO);
    void delete(Long id);

    ProveedorDTO toggleBloqueo(Long proveedorId);

    List<ProveedorDTO> findAllProveedoresActivos();


}