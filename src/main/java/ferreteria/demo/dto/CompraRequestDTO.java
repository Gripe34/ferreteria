package ferreteria.demo.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

public class CompraRequestDTO {

    @NotEmpty(message = "La compra debe contener al menos un producto.")
    private List<ItemCompraDTO> items;

    // Campos de envío o pago (opcionales)
    private String direccionEnvio;
    private String metodoPago;

    // --- Constructor, Getters y Setters ---

    public CompraRequestDTO() {
    }

    public List<ItemCompraDTO> getItems() { return items; }
    public void setItems(List<ItemCompraDTO> items) { this.items = items; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

}
