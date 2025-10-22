package com.certify.backend.dto;

import lombok.*;
import java.sql.Timestamp;

/** Representa los datos de una empresa enviados como respuesta al cliente. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaEmpresa {

    private Integer empresaId;      // ID único de la empresa
    private String ruc;             // RUC de 11 dígitos
    private String razonSocial;     // Nombre legal
    private String correoContacto;  // Correo principal
    private String logoUrl;         // URL del logotipo
    private String estadoNombre;    // Estado: "Activa" o "Inactiva"
    private Timestamp fechaCreacion; // Fecha y hora de creación
}
