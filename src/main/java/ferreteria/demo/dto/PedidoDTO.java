package ferreteria.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private Long clienteId; // O UsuarioDTO, si quieres el objeto completo
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private String estado;
    private String direccionEnvio;
    private String ciudad;
    private String codigoPostal;
    private Long codigoVentaId; // Vínculo con la Venta interna
    private String justificacion;

    // Lista de detalles del pedido
    private List<DetallePedidoDTO> detalles;

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
}