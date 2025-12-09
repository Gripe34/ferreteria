package ferreteria.demo.dto;

import ferreteria.demo.entity.TipoMovimiento;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MovimientoInventarioDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;

    private String justificacion;

    private Long documentoOrigenId;
    private Long usuarioId;
    private String usuarioUsername;
    private Integer stockActual;
    private LocalDateTime fecha;
    private Integer stockAnterior;


    public void setObservaciones(String s) {
    }


}