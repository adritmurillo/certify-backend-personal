package com.certify.backend.repositorio;

import com.certify.backend.modelo.EventoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoCursoRepositorio extends JpaRepository<EventoCurso, Integer> {

}