package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.*;
import com.certify.backend.repositorio.EventoCursoRepositorio;
import com.certify.backend.repositorio.ParticipanteRepositorio;
import com.certify.backend.repositorio.TipoDocumentoRepositorio;
import com.certify.backend.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipanteServicioImpl implements ParticipanteServicio{

    private final ParticipanteRepositorio participanteRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final TipoDocumentoRepositorio tipoDocumentoRepositorio;
    private final EventoCursoRepositorio eventoCursoRepositorio;

    @Override
    @Transactional
    public RespuestaParticipante crearParticipante(PeticionCrearParticipante peticion){

        EventoCurso evento = eventoCursoRepositorio.findById(peticion.getEventoCursoId())
                .orElseThrow(() -> new RuntimeException("El Área/Proyecto con ID: " + peticion.getEventoCursoId() + " no existe."));

        participanteRepositorio.findByPersonaDocumentoAndEventoCurso(peticion.getDocumento(), evento)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("El participante con documento: "+ peticion.getDocumento() + " ya se encuentra en esta Área/Proyecto");
                });

        TipoDocumento tipoDocumento = tipoDocumentoRepositorio.findById(peticion.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id: " +  peticion.getTipoDocumentoId()));

        Usuario usuarioCreador = obtenerUsuarioAutenticado();
        Empresa empresaDelUsuario = usuarioCreador.getEmpresa();

        if (empresaDelUsuario == null) {
            throw new IllegalStateException("El usuario " + usuarioCreador.getCorreo() + " no está asociado a ninguna empresa y no puede registrar participantes.");
        }

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
                .build();

        Participante participanteGuardado = participanteRepositorio.save(nuevoParticipante);

        return RespuestaParticipante.builder()
                // --- Datos para la tabla ---
                .participanteId(participanteGuardado.getParticipanteId())
                .nombreCompleto(participanteGuardado.getPersona().getNombres() + " " + participanteGuardado.getPersona().getApellidos())
                .documento(participanteGuardado.getPersona().getDocumento())
                .correo(participanteGuardado.getCorreoAdicional()) // <-- CORREGIDO
                .empresaNombre(participanteGuardado.getEmpresa().getRazonSocial())
                .areaProyecto(participanteGuardado.getEventoCurso().getNombre()) // <-- CORREGIDO
                .periodoEvento(participanteGuardado.getFechaInicio() + " al " + participanteGuardado.getFechaFin()) // <-- CORREGIDO
                .fechaCreacion(participanteGuardado.getFechaCreacion())

                // --- Datos para el formulario de "Editar" ---
                .nombres(participanteGuardado.getPersona().getNombres())
                .apellidos(participanteGuardado.getPersona().getApellidos())
                .tipoDocumentoId(participanteGuardado.getPersona().getTipoDocumento().getTipoDocumentoId()) // Asume que el getter se llama así
                .eventoCursoId(participanteGuardado.getEventoCurso().getEventoCursoId())
                .fechaInicio(participanteGuardado.getFechaInicio())
                .fechaFin(participanteGuardado.getFechaFin())
                .build();
    }

    // --- El resto de métodos de CRUDService se mantienen igual ---

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

    private Usuario obtenerUsuarioAutenticado(){
        String correoUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado en el contexto de seguridad"));
    }
}