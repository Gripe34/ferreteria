package ferreteria.demo.service;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import java.util.List;

public interface MovimientoInventarioService {

    MovimientoInventarioDTO registrarMovimiento(MovimientoInventarioDTO movimientoDTO);

    List<MovimientoInventarioDTO> findAllMovimientos();

    List<MovimientoInventarioDTO> findAllMovimientosConDetalle();

    MovimientoInventarioDTO findById(Long id);

    List<MovimientoInventarioDTO> getHistorialMovimientosPorProducto(Long productoId);

}