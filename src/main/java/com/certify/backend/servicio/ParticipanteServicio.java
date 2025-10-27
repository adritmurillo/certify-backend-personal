package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.Participante;

public interface ParticipanteServicio extends CRUDService<Participante, Integer>{
    // Creamos un nuevo participante de forma manual
    // hacemos la peticion de los datos del participante y su persona asociada
    // retornamos los datos del participante creado

    RespuestaParticipante crearParticipante(PeticionCrearParticipante peticion);
}
