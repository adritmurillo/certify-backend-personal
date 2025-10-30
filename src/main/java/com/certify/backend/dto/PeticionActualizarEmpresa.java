package com.certify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeticionActualizarEmpresa {

    @NotBlank(message = "La razón social no puede estar vacía.")
    @Size(min = 3, max = 300, message = "La razón social debe tener entre 3 y 300 caracteres.")
    private String razonSocial;

    @NotBlank(message = "El correo de contacto no puede estar vacío.")
    @Email(message = "El formato del correo no es válido.")
    private String correoContacto;
}