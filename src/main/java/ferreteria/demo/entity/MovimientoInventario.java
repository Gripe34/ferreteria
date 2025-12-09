package ferreteria.demo.entity;

import ferreteria.demo.entity.TipoMovimiento; // Asegúrate de ajustar el import si lo mueves
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto; // El producto cuyo stock se afecta

    // Usa el Enum que acabas de crear
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "documento_origen_id")
    private Long documentoOrigenId; // ID de la Venta, Compra, o Ajuste relacionado

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer cantidad; // Cantidad de stock afectada

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(columnDefinition = "TEXT")
    private String observaciones; // Motivo del ajuste/entrada

    @Column(length = 255)
    private String justificacion;

    @Column(name = "stock_anterior", nullable = true)
    private Integer stockAnterior; 

}