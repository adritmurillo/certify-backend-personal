package com.certify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeticionCrearParticipante {
    // Primero se pone los datos de la persona
    @NotBlank(message = "Los nombres son obligatorios.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios.")
    private String apellidos;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 digitos")
    // Este seria para un DNI peruano, si se utilizara otro tipo de documento
    // la logica seria diferente como el de un pasaporte
    private String documento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer tipoDocumentoId;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un formato de correo válido")
    private String correo; // Este correo se puede guardar en correo_adicional

    // Datos del participante

    @NotBlank(message = "El periodo/evento es obligatorio")
    private String periodoEvento;
}
