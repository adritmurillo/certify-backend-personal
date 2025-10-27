package com.certify.backend.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.certify.backend.modelo.TipoDocumento;

import java.util.Optional;

public interface TipoDocumentoRepositorio extends JpaRepository<TipoDocumento, Integer>{
    Optional<TipoDocumento> findByDescripcion(String descripcion);
}
