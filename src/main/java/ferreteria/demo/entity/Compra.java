package ferreteria.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal; // Necesaria para el cálculo del total

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que realiza la compra
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Total de la compra. Tu servicio usa BigDecimal para el cálculo,
    // pero tu entidad usa Double. Asumiremos Double por ahora.
    private Double total;

    private String estado = "COMPLETADA";

    // Mapeo de los ítems de la compra
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles;

    // --- Constructor ---

    public Compra() {
    }

    // --- GETTERS (Necesarios para la vista y para la lógica) ---

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public Double getTotal() {
        return total;
    }

    public String getEstado() {
        return estado;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    // --- SETTERS (Necesarios en CompraServiceImpl para guardar y actualizar) ---

    public void setId(Long id) {
        this.id = id;
    }

    // Resuelve el error 'Cannot resolve method 'setUsuario''
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Resuelve el error 'Cannot resolve method 'setFechaRegistro''
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // Resuelve el error 'Cannot resolve method 'setTotal''
    public void setTotal(Double total) {
        this.total = total;
    }

    // Resuelve el error 'Cannot resolve method 'setEstado''
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }
}