package com.certify.backend.dto;

import lombok.*;

/** DTO para la petición de inicio de sesión. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeticionLogin {

    /** Correo electrónico del usuario. */
    private String correo;

    /** Contraseña del usuario (no debe exponerse en respuestas ni logs). */
    private String contrasena;
}
