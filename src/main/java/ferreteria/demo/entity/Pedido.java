package ferreteria.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el cliente (comprador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDateTime fechaPedido = LocalDateTime.now(); // Se establece al crear

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Estado del ciclo de vida del pedido (EJ: PENDIENTE_PAGO, PAGADO, ENVIADO, CANCELADO)
    private String estado;

    // Campos de dirección de envío (se capturan en el checkout)
    private String direccionEnvio;
    private String ciudad;
    private String codigoPostal;

    // Columna CRÍTICA: Enlace con el ID de la Venta interna
    // Esto vincula el pedido web con la trazabilidad de stock que ya tienes
    @Column(nullable = true)
    private Long codigoVentaId;

    // Los detalles que componen el pedido
    // Inicialización importante para evitar el error de "Cannot invoke add(Object)"
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetallePedido> detalles = new ArrayList<>();
}