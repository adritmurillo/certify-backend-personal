package com.certify.backend.modelo;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participante")
public class Participante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participante_id")
    private Integer participanteId;

    @Column(name = "correo_adicional")
    private String correoAdicional;

    @Column(name = "periodo_evento", nullable = false)
    private String periodoEvento;

    // Aca abajo tenemos a las relaciones con otras entidades

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(cascade = CascadeType.ALL) // Al momento de guardar un participante tambien se guardara la persona (si es nueva)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "creado_por_usuario_id", nullable = false)
    private Usuario creadoPor;

    // Auditoria para saber la fecha en la que fue creado el participante

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private Timestamp fechaCreacion;
}
