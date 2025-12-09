package ferreteria.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el producto afectado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Cantidad que entra o sale. Positivo para entradas, negativo para salidas.
    // Usaremos un Integer simple, ya que el stock de ferretería suele ser entero.
    @Column(name = "cantidad_cambio", nullable = false)
    private Integer cantidadCambio;

    // Tipo de movimiento (ENTRADA_COMPRA, SALIDA_VENTA, DEVOLUCION, AJUSTE_MANUAL)
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private String tipoMovimiento;

    // Fecha y hora exactas del movimiento
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    // Opcional: ID del documento que originó el movimiento (Venta ID, Compra ID, etc.)
    @Column(name = "documento_origen_id")
    private Long documentoOrigenId;

    // Opcional: Referencia a la persona/sistema que lo realizó (Usuario ID)
    @Column(name = "usuario_id")
    private Long usuarioId;
}