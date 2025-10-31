package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionActualizarParticipante;
import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.*;
import com.certify.backend.repositorio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipanteServicioImpl implements ParticipanteServicio {

    private final ParticipanteRepositorio participanteRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final TipoDocumentoRepositorio tipoDocumentoRepositorio;
    private final EventoCursoRepositorio eventoCursoRepositorio;
    private final EstadoRegistroRepositorio estadoRegistroRepositorio;

    @Override
    @Transactional
    public RespuestaParticipante crearParticipante(PeticionCrearParticipante peticion) {
        EventoCurso evento = eventoCursoRepositorio.findById(peticion.getEventoCursoId())
                .orElseThrow(() -> new RuntimeException("El Área/Proyecto con ID: " + peticion.getEventoCursoId() + " no existe."));

        participanteRepositorio.findByPersonaDocumentoAndEventoCurso(peticion.getDocumento(), evento)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("El participante con documento: " + peticion.getDocumento() + " ya se encuentra en esta Área/Proyecto");
                });

        TipoDocumento tipoDocumento = tipoDocumentoRepositorio.findById(peticion.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id: " + peticion.getTipoDocumentoId()));

        Usuario usuarioCreador = obtenerUsuarioAutenticado();
        Empresa empresaDelUsuario = usuarioCreador.getEmpresa();

        if (empresaDelUsuario == null && !"ADMIN".equals(usuarioCreador.getRol().getNombre())) {
            throw new IllegalStateException("El usuario " + usuarioCreador.getCorreo() + " no está asociado a ninguna empresa y no puede registrar participantes.");
        }

        EstadoRegistro estadoActivo = estadoRegistroRepositorio.findByNombre("Activa")
                .orElseThrow(() -> new RuntimeException("El estado 'Activa' no fue encontrado."));

        Persona nuevaPersona = Persona.builder()
                .nombres(peticion.getNombres())
                .apellidos(peticion.getApellidos())
                .documento(peticion.getDocumento())
                .tipoDocumento(tipoDocumento)
                .build();

        Participante nuevoParticipante = Participante.builder()
                .persona(nuevaPersona)
                .empresa(empresaDelUsuario)
                .eventoCurso(evento)
                .correoAdicional(peticion.getCorreo())
                .creadoPor(usuarioCreador)
                .fechaInicio(peticion.getFechaInicio())
                .fechaFin(peticion.getFechaFin())
                .estado(estadoActivo)
                .build();

        Participante participanteGuardado = participanteRepositorio.save(nuevoParticipante);
        return mapearARespuesta(participanteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaParticipante> obtenerPracticantesConFiltros(
            String nombreODni,
            Integer areaProyectoId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Specification<Participante> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if ("Admin Empresa".equals(usuario.getRol().getNombre())) {
                if (usuario.getEmpresa() == null) {
                    return cb.disjunction();
                }
                predicates.add(cb.equal(root.get("empresa").get("empresaId"), usuario.getEmpresa().getEmpresaId()));
            }

            predicates.add(cb.notEqual(root.get("estado").get("nombre"), "Archivado"));

            if (nombreODni != null && !nombreODni.trim().isEmpty()) {
                String filtroLike = "%" + nombreODni.toLowerCase() + "%";
                Predicate pNombre = cb.like(cb.lower(root.get("persona").get("nombres")), filtroLike);
                Predicate pApellido = cb.like(cb.lower(root.get("persona").get("apellidos")), filtroLike);
                Predicate pDocumento = cb.like(root.get("persona").get("documento"), filtroLike);
                predicates.add(cb.or(pNombre, pApellido, pDocumento));
            }

            if (areaProyectoId != null) {
                predicates.add(cb.equal(root.get("eventoCurso").get("eventoCursoId"), areaProyectoId));
            }

            if (fechaInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaInicio"), fechaInicio));
            }

            if (fechaFin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaFin"), fechaFin));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Participante> participantes = participanteRepositorio.findAll(spec);
        return participantes.stream()
                .map(this::mapearARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaParticipante obtenerPorId(Integer id) {
        Participante participante = obtenerYValidarAcceso(id);
        return mapearARespuesta(participante);
    }

    @Override
    @Transactional
    public RespuestaParticipante actualizarParticipante(Integer id, PeticionActualizarParticipante peticion) {
        Participante participante = obtenerYValidarAcceso(id);

        EventoCurso evento = eventoCursoRepositorio.findById(peticion.getEventoCursoId())
                .orElseThrow(() -> new RuntimeException("Área/Proyecto no encontrado."));

        participanteRepositorio.findByPersonaDocumentoAndEventoCurso(peticion.getDocumento(), evento)
                .ifPresent(p -> {
                    if (!p.getParticipanteId().equals(id)) {
                        throw new IllegalArgumentException("El DNI " + peticion.getDocumento() + " ya está registrado para otro participante en esta Área/Proyecto.");
                    }
                });

        TipoDocumento tipoDocumento = tipoDocumentoRepositorio.findById(peticion.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado."));

        Persona persona = participante.getPersona();
        persona.setNombres(peticion.getNombres());
        persona.setApellidos(peticion.getApellidos());
        persona.setDocumento(peticion.getDocumento());
        persona.setTipoDocumento(tipoDocumento);

        participante.setCorreoAdicional(peticion.getCorreo());
        participante.setEventoCurso(evento);
        participante.setFechaInicio(peticion.getFechaInicio());
        participante.setFechaFin(peticion.getFechaFin());

        return mapearARespuesta(participanteRepositorio.save(participante));
    }

    @Override
    @Transactional
    public RespuestaParticipante cambiarEstado(Integer id, String nuevoEstado) {
        if (!nuevoEstado.equals("Activa") && !nuevoEstado.equals("Inactivo")) {
            throw new IllegalArgumentException("El estado solo puede ser 'Activa' o 'Inactivo'.");
        }
        Participante participante = obtenerYValidarAcceso(id);
        EstadoRegistro estado = estadoRegistroRepositorio.findByNombre(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado '" + nuevoEstado + "' no encontrado."));

        participante.setEstado(estado);
        return mapearARespuesta(participanteRepositorio.save(participante));
    }

    @Override
    @Transactional
    public void eliminarParticipante(Integer id) {
        Participante participante = obtenerYValidarAcceso(id);
        EstadoRegistro estadoArchivado = estadoRegistroRepositorio.findByNombre("Archivado")
                .orElseThrow(() -> new RuntimeException("Estado 'Archivado' no encontrado."));
        participante.setEstado(estadoArchivado);
        participanteRepositorio.save(participante);
    }

    private Participante obtenerYValidarAcceso(Integer participanteId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Participante participante = participanteRepositorio.findById(participanteId)
                .orElseThrow(() -> new RuntimeException("Participante con ID " + participanteId + " no encontrado."));

        if ("ADMIN".equals(usuario.getRol().getNombre())) {
            return participante;
        }

        if (usuario.getEmpresa() == null || !participante.getEmpresa().getEmpresaId().equals(usuario.getEmpresa().getEmpresaId())) {
            throw new SecurityException("No tiene permiso para acceder a este participante.");
        }
        return participante;
    }

    private RespuestaParticipante mapearARespuesta(Participante p) {
        String periodo = (p.getFechaInicio() != null && p.getFechaFin() != null) ?
                p.getFechaInicio().toString() + " al " + p.getFechaFin().toString() : "N/A";

        return RespuestaParticipante.builder()
                .participanteId(p.getParticipanteId())
                .nombreCompleto(p.getPersona().getNombres() + " " + p.getPersona().getApellidos())
                .documento(p.getPersona().getDocumento())
                .correo(p.getCorreoAdicional())
                .empresaNombre(p.getEmpresa() != null ? p.getEmpresa().getRazonSocial() : "N/A (Admin)")
                .areaProyecto(p.getEventoCurso().getNombre())
                .periodoEvento(periodo)
                .estadoNombre(p.getEstado().getNombre())
                .fechaCreacion(p.getFechaCreacion())
                .nombres(p.getPersona().getNombres())
                .apellidos(p.getPersona().getApellidos())
                .tipoDocumentoId(p.getPersona().getTipoDocumento().getTipoDocumentoId())
                .eventoCursoId(p.getEventoCurso().getEventoCursoId())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .build();
    }

    @Override
    public Participante save(Participante entity) {
        return participanteRepositorio.save(entity);
    }

    @Override
    public Participante saveAndFlush(Participante entity) {
        return participanteRepositorio.saveAndFlush(entity);
    }

    @Override
    public List<Participante> findAll() {
        return participanteRepositorio.findAll();
    }

    @Override
    public List<Participante> saveAll(List<Participante> entities) {
        return participanteRepositorio.saveAll(entities);
    }

    @Override
    public Optional<Participante> findById(Integer id) {
        return participanteRepositorio.findById(id);
    }

    private Usuario obtenerUsuarioAutenticado() {
        String correoUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el contexto de seguridad"));
    }
}