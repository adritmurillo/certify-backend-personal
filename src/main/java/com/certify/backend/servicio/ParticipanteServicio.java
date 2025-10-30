package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.Participante;

public interface ParticipanteServicio extends CRUDService<Participante, Integer>{

    RespuestaParticipante crearParticipante(PeticionCrearParticipante peticion);
}
