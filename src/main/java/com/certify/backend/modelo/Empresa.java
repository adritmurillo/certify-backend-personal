package com.certify.backend.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

/**
 * Entidad que representa la tabla 'empresa'.
 * Contiene información general de la empresa y relaciones básicas.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empresa_id")
    private Integer empresaId;

    @Column(name = "ruc", unique = true, nullable = false, length = 11)
    private String ruc;

    @Column(name = "razon_social", unique = true, nullable = false, length = 300)
    private String razonSocial;

    @Column(name = "correo_contacto", unique = true, nullable = false)
    private String correoContacto;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /** Estado actual de la empresa (ej. Activa, Inactiva). */
    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoRegistro estado;

    // --- NUEVO CAMPO PARA MOTIVO DE RECHAZO ---
    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    /** Usuario que registró la empresa. */
    @ManyToOne
    @JoinColumn(name = "creado_por_usuario_id", nullable = true)
    private Usuario creadoPor;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Usuario> usuarios;


    /** Auditoría de creación. */
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private Timestamp fechaCreacion;

    /** Auditoría de última actualización. */
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;
}
