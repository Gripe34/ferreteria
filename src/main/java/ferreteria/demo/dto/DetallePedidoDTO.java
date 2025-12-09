package ferreteria.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetallePedidoDTO {

    private Long id;
    private Long pedidoId; // El ID del Pedido al que pertenece

    // Información del Producto
    private Long productoId;
    private String nombreProducto; // Útil para la vista

    private Integer cantidad;
    private BigDecimal precioUnitarioVenta;

    // Método para calcular el subtotal (aunque ya se calcula en el front/service, es bueno tenerlo aquí)
    public BigDecimal getSubtotal() {
        if (precioUnitarioVenta == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitarioVenta.multiply(new BigDecimal(cantidad));
    }
}