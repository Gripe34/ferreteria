package ferreteria.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ItemCarritoDTO {

    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;

    private Integer cantidad;

    public ItemCarritoDTO() {
        this.cantidad = 0; // Forzar la cantidad a cero
    }

    // Método para calcular el subtotal de este ítem
    public BigDecimal getSubtotal() {
        if (precioUnitario == null || this.cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(new BigDecimal(this.cantidad));
    }
}