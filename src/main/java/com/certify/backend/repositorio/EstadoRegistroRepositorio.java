package com.certify.backend.repositorio;

import com.certify.backend.modelo.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EstadoRegistroRepositorio extends JpaRepository<EstadoRegistro, Integer> {

    Optional<EstadoRegistro> findByNombre(String nombre);
}
