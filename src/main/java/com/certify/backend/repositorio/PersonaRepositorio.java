package com.certify.backend.repositorio;

import com.certify.backend.modelo.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Persona.
 *
 * Permite realizar operaciones CRUD y consultas personalizadas
 * sobre la tabla "personas".
 */
@Repository
public interface PersonaRepositorio extends JpaRepository<Persona, Integer> {
    // Por ahora no necesitamos métodos personalizados,
}
