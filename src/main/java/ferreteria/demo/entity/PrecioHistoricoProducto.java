package ferreteria.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "precio_historico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrecioHistoricoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Valores Anteriores
    @Column(precision = 10, scale = 2)
    private BigDecimal precioAnterior;

    @Column(precision = 10, scale = 2)
    private BigDecimal costoAnterior;

    @Column
    private Integer stockAnterior; // 🚀 NUEVO: Stock que tenía antes de la edición

    // Valores Nuevos
    @Column(precision = 10, scale = 2)
    private BigDecimal precioNuevo;

    @Column(precision = 10, scale = 2)
    private BigDecimal costoNuevo;

    @Column
    private Integer stockNuevo; // 🚀 NUEVO: Stock que tiene después de la edición

    @Column(nullable = false)
    private LocalDateTime fechaCambio = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Long getUsuarioId() {
        return null;
    }
}