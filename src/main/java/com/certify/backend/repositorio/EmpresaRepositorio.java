package com.certify.backend.repositorio;

import com.certify.backend.modelo.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para gestionar las operaciones CRUD sobre la entidad Empresa.
 *
 * Gracias a JpaRepository, se heredan métodos como:
 *  - findAll(), findById(), save(), deleteById(), etc.
 *
 * Además, se definen consultas personalizadas mediante el nombre del método.
 */
@Repository
public interface EmpresaRepositorio extends JpaRepository<Empresa, Integer> {

    /**
     * Busca una empresa por su número de RUC.
     *
     * @param ruc Número de RUC de la empresa.
     * @return Un Optional que contiene la empresa si existe, o vacío si no.
     */
    Optional<Empresa> findByRuc(String ruc);
}
