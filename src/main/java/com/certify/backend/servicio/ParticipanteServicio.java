package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionActualizarParticipante;
import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.Participante;

import java.time.LocalDate;
import java.util.List;

public interface ParticipanteServicio extends CRUDService<Participante, Integer>{

    RespuestaParticipante crearParticipante(PeticionCrearParticipante peticion);
    List<RespuestaParticipante> obtenerPracticantesConFiltros(
            String nombreODni,
            Integer areaProyectoId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    RespuestaParticipante obtenerPorId(Integer id);
    RespuestaParticipante actualizarParticipante(Integer id, PeticionActualizarParticipante peticion);
    RespuestaParticipante cambiarEstado(Integer id, String nuevoEstado);
    void eliminarParticipante(Integer id); // Será un Soft Delete
}
