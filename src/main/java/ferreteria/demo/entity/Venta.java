package ferreteria.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime; // Importación necesaria
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data // Lombok genera todos los getters/setters
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha; // Campo antiguo que sigue siendo NOT NULL en la DB

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // 🔥 CAMPO FALTANTE/CRÍTICO: Fecha y hora para auditoría (Factura/Reporte)
    // Asumimos que esta es la fecha real que quieres usar.
    @Column(name = "fecha_registro", nullable = true)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(length = 20, nullable = true)
    private String metodoPago;


}