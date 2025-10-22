package com.certify.backend.repositorio;

import com.certify.backend.modelo.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad EstadoRegistro.
 *
 * Proporciona operaciones CRUD y consultas personalizadas sobre la tabla "estado_registro".
 */
@Repository
public interface EstadoRegistroRepositorio extends JpaRepository<EstadoRegistro, Integer> {

    /**
     * Busca un estado de registro por su nombre.
     *
     * @param nombre Nombre del estado (por ejemplo: "Activo", "Inactivo").
     * @return Un Optional con el estado encontrado, o vacío si no existe.
     */
    Optional<EstadoRegistro> findByNombre(String nombre);
}
