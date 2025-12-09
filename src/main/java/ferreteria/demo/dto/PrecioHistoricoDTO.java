package ferreteria.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrecioHistoricoDTO {
    private Long id;
    private LocalDateTime fechaCambio;
    private String usuarioUsername; // Quién hizo el cambio

    // Valores Anteriores
    private BigDecimal precioAnterior;
    private BigDecimal costoAnterior;
    private Integer stockAnterior;

    // Valores Nuevos
    private BigDecimal precioNuevo;
    private BigDecimal costoNuevo;
    private Integer stockNuevo;

    // ------------------------------------
    // Getters y Setters Corregidos
    // ------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 🔥 Corrección para el error de fechaCambio 🔥
    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    // Getters/Setters para Usuario
    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    // Getters/Setters para Valores Anteriores
    public BigDecimal getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(BigDecimal precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public BigDecimal getCostoAnterior() {
        return costoAnterior;
    }

    public void setCostoAnterior(BigDecimal costoAnterior) {
        this.costoAnterior = costoAnterior;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    // Getters/Setters para Valores Nuevos
    public BigDecimal getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(BigDecimal precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    public BigDecimal getCostoNuevo() {
        return costoNuevo;
    }

    public void setCostoNuevo(BigDecimal costoNuevo) {
        this.costoNuevo = costoNuevo;
    }

    public Integer getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }
}