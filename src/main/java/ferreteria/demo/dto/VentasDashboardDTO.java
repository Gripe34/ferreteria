package ferreteria.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections; // Para inicializar listas vacías

@Data
public class VentasDashboardDTO {

    // Ventas hechas por clientes en el portal online (si aplica)
    private List<VentaDTO> ventasWeb = Collections.emptyList();

    // Ventas hechas por el vendedor en caja
    private List<VentaDTO> ventasCajaEfectivo = Collections.emptyList();
    private List<VentaDTO> ventasCajaTarjeta = Collections.emptyList();

    // Opcional: Totales
    private BigDecimal totalGeneral;
}