package com.certify.backend.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id", referencedColumnName = "persona_id", nullable = false, unique = true)
    private Persona persona;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String contrasena;

    @ManyToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "rol_id", nullable = false)
    private Rol rol;

    // METODOS DE USERDETAILS

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.getNombre()));
    }

    @Override
    public String getPassword() {
        // Spring Security busca este método para obtener la contraseña.
        return contrasena;
    }

    @Override
    public String getUsername(){
        // Spring Security busca este método para obtener el nombre de usuario (nuestro correo).
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
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