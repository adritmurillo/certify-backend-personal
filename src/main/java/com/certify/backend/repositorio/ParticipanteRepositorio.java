package com.certify.backend.repositorio;

import com.certify.backend.modelo.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipanteRepositorio extends JpaRepository<Participante, Integer> {
    /**
     * Busca un participante por el documento de su persona y el periodo del evento.
     * Esto nos ayudará a cumplir el Criterio de Aceptación #3 de la HU5:
     * "Evitar duplicados dentro del mismo 'evento/periodo' (DNI o correo)".
     */

    Optional<Participante> findByPersonaDocumentoAndPeriodoEvento(String documento, String periodoEvento);
}
