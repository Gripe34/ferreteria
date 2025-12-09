package ferreteria.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime; // Usar LocalDateTime para hora y fecha
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private Long id;

    private LocalDateTime fechaRegistro;

    private BigDecimal total;
    private Long clienteId;
    private String vendedorNombre;
    private String clienteNombre;

    private LocalDate fecha;

    private List<DetalleVentaDTO> detalles;

    private String metodoPago;

    private String clienteUsername;


    public LocalDate getFecha() {
        return fecha;
    }


    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getClienteUsername() {
        return clienteUsername;
    }

    public void setClienteUsername(String clienteUsername) {
        this.clienteUsername = clienteUsername;
    }

}