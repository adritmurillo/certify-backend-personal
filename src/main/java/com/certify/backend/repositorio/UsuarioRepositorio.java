package com.certify.backend.repositorio;

import com.certify.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    // Spring data JPA nos ayudara a buscar un usuario mediante su correo
    Optional<Usuario> findByCorreo(String correo);
}
