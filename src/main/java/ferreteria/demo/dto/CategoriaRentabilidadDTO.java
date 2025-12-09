package ferreteria.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategoriaRentabilidadDTO {
    private String nombreCategoria;
    private BigDecimal gananciaTotal;
}