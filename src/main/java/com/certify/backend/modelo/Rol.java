package com.certify.backend.modelo;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId; // Identificador único del rol

    @Column(unique = true, nullable = false, length = 50)
    private String nombre; // Nombre del rol (debe ser único)

    @Column(length = 255)
    private String descripcion; // Descripción breve del rol
}
