package com.certify.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeticionActualizarParticipante {

    @NotBlank(message = "Los nombres son obligatorios.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios.")
    private String apellidos;

    @NotBlank(message = "El documento es obligatorio.")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos.")
    private String documento;

    @NotNull(message = "El tipo de documento es obligatorio.")
    private Integer tipoDocumentoId;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Debe ser un formato de correo válido.")
    private String correo;

    @NotNull(message = "El ID del Área/Proyecto es obligatorio.")
    private Integer eventoCursoId;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;
}