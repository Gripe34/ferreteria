package ferreteria.demo.dto;

import ferreteria.demo.entity.Rol;
import lombok.Data;


@Data
public class UsuarioCreateDTO {
    private String username;
    private String password;
    private Rol rol;
}