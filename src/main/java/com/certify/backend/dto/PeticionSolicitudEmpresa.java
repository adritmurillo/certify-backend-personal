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

public class PeticionSolicitudEmpresa {
    @NotBlank(message = "El RUC no puede estar vacío.")
    @Size(min = 11, max = 11, message = "El RUC debe tener 11 dígitos.")
    private String ruc;

    @NotBlank(message = "La razón social no puede estar vacía.")
    private String razonSocial;

    @NotBlank(message = "El correo de contacto de la empresa no puede estar vacío.")
    @Email(message = "El formato del correo de contacto no es válido.")
    private String correoContacto;

    // --- Datos del Primer Administrador ---
    @NotBlank(message = "El nombre del administrador es obligatorio.")
    private String nombresAdmin;

    @NotBlank(message = "El apellido del administrador es obligatorio.")
    private String apellidosAdmin;

    @NotBlank(message = "El documento del administrador es obligatorio.")
    @Size(min = 8, max = 8, message = "El DNI del administrador debe tener 8 dígitos.")
    private String documentoAdmin;

    @NotBlank(message = "El correo del administrador es obligatorio.")
    @Email(message = "El formato del correo del administrador no es válido.")
    private String correoAdmin;
}
