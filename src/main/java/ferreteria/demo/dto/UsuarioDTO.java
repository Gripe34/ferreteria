package ferreteria.demo.dto;

import ferreteria.demo.entity.Rol;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // 🔥 Lombok genera los getters (incluyendo getEmail)
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private Rol rol;
    private String email;


}