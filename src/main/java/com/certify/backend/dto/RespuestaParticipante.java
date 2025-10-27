package com.certify.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaParticipante {
    private Integer participanteId;
    private String nombreCompleto;
    private String documento;
    private String periodoEvento;
    private String empresaNombre;
    private Timestamp fechaCreacion;
}
