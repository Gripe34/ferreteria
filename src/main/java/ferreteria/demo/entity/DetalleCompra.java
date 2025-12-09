package ferreteria.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal; // Importación necesaria si cambias a BigDecimal

@Entity
@Table(name = "detalles_compra")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación de vuelta a la Compra
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra; // <-- Necesita setCompra(Compra compra)

    // Relación al producto que se compró
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private Integer cantidad; // <-- Necesita getCantidad()

    // El precio al que se vendió (importante para el historial)
    // NOTA: Para cálculos financieros, 'Double' puede ser impreciso.
    // Usaremos Double aquí para coincidir con tu código, pero BigDecimal es mejor.
    private Double precioUnitario; // <-- Necesita getPrecioUnitario()

    // --- Constructor, Getters y Setters ---

    public DetalleCompra() {
    }

    // --- GETTERS (Necesarios en CompraServiceImpl) ---

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    // Si hubieras usado BigDecimal:
    /*
    public BigDecimal getPrecioUnitario() {
        // Debes implementar la conversión si usaste Double
        return (this.precioUnitario != null) ? BigDecimal.valueOf(this.precioUnitario) : BigDecimal.ZERO;
    }
    */


    // --- SETTERS (Necesarios en CompraServiceImpl) ---

    public void setCompra(Compra compra) {
        this.compra = compra; // Resuelve el error 'Cannot resolve method 'setCompra''
    }

    // Incluyo setCantidad y setPrecioUnitario por si los necesitas, aunque el error se enfocaba en los getters
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // --- Otros Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compra getCompra() {
        return compra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

}