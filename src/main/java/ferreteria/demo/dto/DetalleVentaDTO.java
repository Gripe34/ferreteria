package ferreteria.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {

    private Long id;


    private Integer cantidad;
    private BigDecimal precioUnitario;

    // Campos necesarios para mostrar la auditoría:
    private Long productoId;
    private String productoNombre;
}