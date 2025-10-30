package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionActualizarParticipante; // <-- NUEVO IMPORT
import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.*;
import com.certify.backend.repositorio.*; // <-- NUEVO IMPORT
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // <-- NUEVO IMPORT
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

        if (empresaDelUsuario == null && !"ADMIN".equals(usuarioCreador.getRol().getNombre())) { // <-- MODIFICACIÓN LIGERA (Permite al ADMIN crear)
            throw new IllegalStateException("El usuario " + usuarioCreador.getCorreo() + " no está asociado a ninguna empresa y no puede registrar participantes.");
        }

        // --- MODIFICACIÓN CLAVE (1): Buscar el estado "Activa" ---
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
                .empresa(empresaDelUsuario) // <-- Será null si el creador es ADMIN
                .eventoCurso(evento)
                .correoAdicional(peticion.getCorreo())
                .creadoPor(usuarioCreador)
                .fechaInicio(peticion.getFechaInicio())
                .fechaFin(peticion.getFechaFin())
                .estado(estadoActivo) // <-- MODIFICACIÓN CLAVE (2): Asignar el estado
                .build();

        Participante participanteGuardado = participanteRepositorio.save(nuevoParticipante);

        // --- MODIFICACIÓN CLAVE (3): Usar el método helper ---
        return mapearARespuesta(participanteGuardado);
    }

    // --- MÉTODOS NUEVOS AÑADIDOS ---

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaParticipante> obtenerTodosPorEmpresa() {
        Usuario usuario = obtenerUsuarioAutenticado();

        // Lógica para Superadmin (ADMIN)
        if ("ADMIN".equals(usuario.getRol().getNombre())) {
            return participanteRepositorio.findAllByEstado_NombreNot("Archivado")
                    .stream()
                    .map(this::mapearARespuesta)
                    .toList();
        }

        // Lógica para Admin Empresa
        if (usuario.getEmpresa() == null) {
            return List.of(); // Admin de empresa sin empresa asignada, no debe ver nada.
        }
        Integer empresaId = usuario.getEmpresa().getEmpresaId();

        return participanteRepositorio.findAllByEmpresa_EmpresaIdAndEstado_NombreNot(empresaId, "Archivado")
                .stream()
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

        // Validar que el nuevo DNI no esté en uso por OTRO participante en el mismo curso
        participanteRepositorio.findByPersonaDocumentoAndEventoCurso(peticion.getDocumento(), evento)
                .ifPresent(p -> {
                    if (!p.getParticipanteId().equals(id)) {
                        throw new IllegalArgumentException("El DNI " + peticion.getDocumento() + " ya está registrado para otro participante en esta Área/Proyecto.");
                    }
                });

        TipoDocumento tipoDocumento = tipoDocumentoRepositorio.findById(peticion.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado."));

        // Actualizar datos de la Persona
        Persona persona = participante.getPersona();
        persona.setNombres(peticion.getNombres());
        persona.setApellidos(peticion.getApellidos());
        persona.setDocumento(peticion.getDocumento());
        persona.setTipoDocumento(tipoDocumento);

        // Actualizar datos del Participante
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

    // --- MÉTODOS PRIVADOS DE AYUDA (HELPERS) ---

    private Participante obtenerYValidarAcceso(Integer participanteId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Participante participante = participanteRepositorio.findById(participanteId)
                .orElseThrow(() -> new RuntimeException("Participante con ID " + participanteId + " no encontrado."));

        // El Superadmin (rol ADMIN) tiene acceso a todo.
        if ("ADMIN".equals(usuario.getRol().getNombre())) {
            return participante;
        }

        // El Admin de Empresa solo puede ver los de su empresa
        if (usuario.getEmpresa() == null || !participante.getEmpresa().getEmpresaId().equals(usuario.getEmpresa().getEmpresaId())) {
            throw new SecurityException("No tiene permiso para acceder a este participante.");
        }
        return participante;
    }

    // Un solo lugar para convertir la entidad al DTO de respuesta
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


    // --- El resto de tus métodos de CRUDService (pueden quedar así) ---

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
