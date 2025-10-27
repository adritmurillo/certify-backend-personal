package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.modelo.*;
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

    @Override
    @Transactional // Operacion atomica, o todo o nada
    public RespuestaParticipante crearParticipante(PeticionCrearParticipante peticion){
        participanteRepositorio.findByPersonaDocumentoAndPeriodoEvento(peticion.getDocumento(), peticion.getPeriodoEvento())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("El participante con documento: "+ peticion.getDocumento() + " ya se encuentra en el periodo/evento");
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
        Participante nuevaParticipante = Participante.builder()
                .persona(nuevaPersona)
                .empresa(empresaDelUsuario)
                .periodoEvento(peticion.getPeriodoEvento())
                .correoAdicional(peticion.getCorreo())
                .creadoPor(usuarioCreador)
                .build();

        Participante participanteGuardado = participanteRepositorio.save(nuevaParticipante);

        return RespuestaParticipante.builder()
                .participanteId(participanteGuardado.getParticipanteId())
                .nombreCompleto(participanteGuardado.getPersona().getNombres() + " " + participanteGuardado.getPersona().getApellidos())
                .documento(participanteGuardado.getPersona().getDocumento())
                .periodoEvento(participanteGuardado.getPeriodoEvento())
                .empresaNombre(participanteGuardado.getEmpresa().getRazonSocial())
                .fechaCreacion(participanteGuardado.getFechaCreacion())
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

    private Usuario obtenerUsuarioAutenticado(){
        String correoUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado en el contexto de seguridad"));
    }
}
