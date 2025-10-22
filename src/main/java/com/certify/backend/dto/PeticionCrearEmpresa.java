package com.certify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** DTO para recibir los datos necesarios al crear una empresa. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeticionCrearEmpresa {

    @NotBlank(message = "El RUC no puede estar vacío.")
    @Size(min = 11, max = 11, message = "El RUC debe tener exactamente 11 dígitos.")
    private String ruc;

    @NotBlank(message = "La razón social no puede estar vacía.")
    private String razonSocial;

    @NotBlank(message = "El correo de contacto no puede estar vacío.")
    @Email(message = "El formato del correo no es válido.")
    private String correoContacto;
}
