package com.certify.backend.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Representa un usuario del sistema. Implementa UserDetails para integrarse con Spring Security. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    /** Información personal del usuario (vinculada 1:1 con Persona). */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private Persona persona;

    /** Correo electrónico único, usado como nombre de usuario. */
    @Column(unique = true, nullable = false)
    private String correo;

    /** Contraseña encriptada. */
    @Column(name = "password_hash", nullable = true)
    private String contrasena;

    /** Rol asignado al usuario (ADMIN, EMPLEADO, etc.). */
    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Métodos requeridos por la interfaz UserDetails de Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.getNombre()));
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cambia según reglas de negocio
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
