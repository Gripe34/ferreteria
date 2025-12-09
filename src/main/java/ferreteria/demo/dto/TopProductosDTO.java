package ferreteria.demo.dto;

import java.math.BigDecimal;
import java.util.ListIterator; // Esta importación parece innecesaria aquí

public class TopProductosDTO {
    // --- PROPIEDADES ---
    private String nombreProducto;
    private BigDecimal gananciaTotal;
    private Long cantidadVendida; // Mantengo Long para el conteo de la BD

    // --- GETTERS & SETTERS (CORREGIDOS) ---

    // Setter para nombreProducto
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    // Setter para gananciaTotal
    public void setGananciaTotal(BigDecimal gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }

    // ✅ CORRECCIÓN CLAVE: El setter ahora acepta 'long' o 'Long'
    // Como la propiedad es 'Long', es mejor usar 'Long' o aceptar 'long' y dejar que Java haga el autoboxing.
    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    // ✅ CORRECCIÓN: El getter debe devolver el tipo correcto (BigDecimal), no ListIterator
    public BigDecimal getGananciaTotal() {
        return gananciaTotal;
    }

    // ✅ CORRECCIÓN: El getter debe devolver el tipo correcto (Long o long)
    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    // Si necesitas el getter para el nombre, agrégalo:
    public String getNombreProducto() {
        return nombreProducto;
    }

    // NOTA: Si usaras Lombok (@Data), todo esto se generaría automáticamente.
}