package com.certify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeticionCrearParticipante {
    @NotBlank(message = "Los nombres son obligatorios.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios.")
    private String apellidos;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    private String documento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer tipoDocumentoId;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un formato de correo válido")
    private String correo; // Este correo se guarda en correo_adicional

    @NotNull(message = "El ID del Área/Proyecto es obligatorio.")
    private Integer eventoCursoId;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;
}