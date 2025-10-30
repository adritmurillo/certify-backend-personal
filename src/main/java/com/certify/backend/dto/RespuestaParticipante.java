package com.certify.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaParticipante {

    // --- DATOS PARA MOSTRAR EN LA TABLA ---
    private Integer participanteId;
    private String nombreCompleto;
    private String documento;
    private String correo;
    private String empresaNombre;
    private String areaProyecto;
    private String periodoEvento;
    private String estadoNombre;
    private Timestamp fechaCreacion;


    // --- DATOS ADICIONALES PARA EL FORMULARIO DE "EDITAR" ---
    private String nombres;
    private String apellidos;
    private Integer tipoDocumentoId;
    private Integer eventoCursoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}