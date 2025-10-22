package com.certify.backend.modelo;

import jakarta.persistence.*;
import lombok.*;

/** Representa el estado de un registro (por ejemplo: Activo, Inactivo, Eliminado). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estado_registro")
public class EstadoRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer estadoId; // Identificador único del estado

    @Column(nullable = false, length = 50)
    private String nombre; // Nombre del estado
}
