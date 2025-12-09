package ferreteria.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TendenciaVentaDTO {
    private String periodo; // Ej: "Nov 2025"
    private BigDecimal totalVenta;
}