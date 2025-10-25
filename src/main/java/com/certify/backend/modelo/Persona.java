package com.certify.backend.modelo;

import jakarta.persistence.*;
import lombok.*;

/** Representa a una persona registrada en el sistema (natural o vinculada a una empresa). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Integer personaId; // Identificador único de la persona

    @Column(unique = true, nullable = false, length = 20)
    private String documento; // Documento de identidad (DNI, CE, etc.)

    @Column(nullable = false)
    private String nombres; // Nombres de la persona

    @Column(nullable = false)
    private String apellidos; // Apellidos de la persona
    
    @ManyToOne
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumento tipoDocumento;
}
