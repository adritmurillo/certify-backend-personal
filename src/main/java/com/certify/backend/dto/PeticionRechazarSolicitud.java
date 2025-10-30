package com.certify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PeticionRechazarSolicitud {

    @NotBlank(message = "El motivo del rechazo no puede estar vacío.")
    private String motivo;


}