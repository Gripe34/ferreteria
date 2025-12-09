package ferreteria.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String contactoEmail;
    private String contactoTelefono;

    @Column(nullable = false)
    private boolean activo = true;  

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // --- Constructor (Recomendado, aunque no obligatorio para este fix) ---
    public Proveedor() {}

    // --- Getters y Setters (Corregidos) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // 🔥 MÉTODOS REQUERIDOS POR EL DTO/SERVICE 🔥
    public String getContactoEmail() { return contactoEmail; }
    public void setContactoEmail(String contactoEmail) { this.contactoEmail = contactoEmail; }

    public String getContactoTelefono() { return contactoTelefono; }
    public void setContactoTelefono(String contactoTelefono) { this.contactoTelefono = contactoTelefono; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}