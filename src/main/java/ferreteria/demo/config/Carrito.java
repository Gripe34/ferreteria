package ferreteria.demo.config;

import ferreteria.demo.dto.ItemCarritoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@SessionScope
public class Carrito implements Serializable {

    private Map<Long, ItemCarritoDTO> items = new HashMap<>();

    public void agregarItem(ItemCarritoDTO item) {
        if (items.containsKey(item.getProductoId())) {
            ItemCarritoDTO existingItem = items.get(item.getProductoId());
            existingItem.setCantidad(existingItem.getCantidad() + item.getCantidad());
        } else {
            items.put(item.getProductoId(), item);
        }
    }

    public void removerItem(Long productoId) {
        items.remove(productoId);
    }

    public void limpiarCarrito() {
        items.clear();
    }

    /**
     * Implementación del método necesario para la edición rápida en el carrito.
     */
    public void actualizarItem(Long productoId, int cantidad) {
        if (items.containsKey(productoId)) {
            ItemCarritoDTO existingItem = items.get(productoId);
            if (cantidad > 0) {
                existingItem.setCantidad(cantidad);
            } else {
                items.remove(productoId); // Si la cantidad es 0 o menos, lo eliminamos.
            }
        }
    }

    public BigDecimal getTotalCarrito() {
        return items.values().stream()
                .map(ItemCarritoDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCantidadTotal() {
        return items.values().stream()
                .mapToInt(ItemCarritoDTO::getCantidad)
                .sum();
    }

    // Getters y Setters
    public Map<Long, ItemCarritoDTO> getItems() {
        return items;
    }

    public void setItems(Map<Long, ItemCarritoDTO> items) {
        this.items = items;
    }
}