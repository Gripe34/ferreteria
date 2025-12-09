package ferreteria.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GraficaVentaDTO {
    private String nombreProducto;
    private BigDecimal gananciaTotal;
    private Long cantidadVendida;

    public Long getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }
}