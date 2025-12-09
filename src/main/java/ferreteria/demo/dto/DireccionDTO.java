package ferreteria.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DireccionDTO {

    @NotBlank
    private String callePrincipal;

    @NotBlank
    private String ciudad;

    @NotBlank
    private String codigoPostal;

    private String referencias; // Opcional
}