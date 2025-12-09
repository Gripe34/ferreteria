package ferreteria.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistorialPrecioDTO {

    private Long id;
    private Long productoId;
    private String usuarioUsername;

    private BigDecimal costoAnterior;
    private BigDecimal costoNuevo;
    private BigDecimal precioAnterior;
    private BigDecimal precioNuevo;
    private Integer stockAnterior;
    private Integer stockNuevo;

    private LocalDateTime fechaCambio;

    // 🔥 NUEVA PROPIEDAD: Justificación para mostrar en la tabla de auditoría
    private String justificacion;

    // Constructor, Getters y Setters...
    // (Asegúrate de tener el getter y setter para 'justificacion')


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public BigDecimal getCostoAnterior() {
        return costoAnterior;
    }

    public void setCostoAnterior(BigDecimal costoAnterior) {
        this.costoAnterior = costoAnterior;
    }

    public BigDecimal getCostoNuevo() {
        return costoNuevo;
    }

    public void setCostoNuevo(BigDecimal costoNuevo) {
        this.costoNuevo = costoNuevo;
    }

    public BigDecimal getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(BigDecimal precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public BigDecimal getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(BigDecimal precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    // 🔥 Getters y Setters para la justificación
    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
}