package com.certify.backend.repositorio;

import com.certify.backend.modelo.EventoCurso;
import com.certify.backend.modelo.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipanteRepositorio extends JpaRepository<Participante, Integer> {

    Optional<Participante> findByPersonaDocumentoAndEventoCurso(String personaDocumento, EventoCurso eventoCurso);
    List<Participante> findAllByEmpresa_EmpresaIdAndEstado_NombreNot(Integer empresaId, String estadoNombre);
    List<Participante> findAllByEstado_NombreNot(String estadoNombre);
}
