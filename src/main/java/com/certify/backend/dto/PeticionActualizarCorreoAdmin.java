package com.certify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PeticionActualizarCorreoAdmin {

    @NotBlank(message = "El nuevo correo no puede estar vacío.")
    @Email(message = "El formato del correo no es válido.")
    private String nuevoCorreo;
}