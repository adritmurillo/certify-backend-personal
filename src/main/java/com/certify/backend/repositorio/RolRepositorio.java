package com.certify.backend.repositorio;

import com.certify.backend.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Rol.
 *
 * Permite realizar operaciones CRUD sobre la tabla "rol"
 * y facilita la creación de consultas personalizadas.
 */
@Repository
public interface RolRepositorio extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
}
