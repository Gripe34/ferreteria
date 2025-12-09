package ferreteria.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data // Genera getters, setters, toString, etc.
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // Credencial principal

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) // <-- CORRECCIÓN: Aumentar la longitud aquí
    private Rol rol;;

    @Column(nullable = true, unique = true) // Generalmente es UNIQUE, pero lo dejaremos NULLABLE por si acaso
    private String email;
}
