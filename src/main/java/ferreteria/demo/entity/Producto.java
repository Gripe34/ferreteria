package ferreteria.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private BigDecimal precio;
    private BigDecimal costo;
    private Integer stock;

    @Column(length = 500, nullable = true) 
    private String imageUrl;

    // Campos de auditoría/trazabilidad
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaModificacion;

    // Relaciones:

    // Relación con Categoría (EXISTENTE)
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // El cambio clave: ahora es Boolean objeto, consistente con los Getters/Setters.
    @Column(nullable = false)
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = true)
    private Proveedor proveedor;

    // El cambio clave: ahora es Boolean objeto, consistente con los Getters/Setters.
    @Column(name = "bloqueado")
    private Boolean bloqueado;


    // --- Constructor (Recomendado) ---
    public Producto() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // GETTER CORREGIDO: Retorna Boolean objeto
    public Boolean getActivo() { 
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    // GETTER CORREGIDO: Retorna Boolean objeto
    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}