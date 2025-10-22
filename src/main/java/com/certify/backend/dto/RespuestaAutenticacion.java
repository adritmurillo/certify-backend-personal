package com.certify.backend.dto;

import lombok.*;

/** Respuesta enviada tras un inicio de sesión exitoso. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaAutenticacion {

    /** Token JWT generado para el usuario autenticado. */
    private String token;
}
