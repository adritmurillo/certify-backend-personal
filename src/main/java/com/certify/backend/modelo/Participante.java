package com.certify.backend.modelo;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_curso_id", nullable = false)
    private EventoCurso eventoCurso;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

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
