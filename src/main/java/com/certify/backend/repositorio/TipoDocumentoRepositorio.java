package com.certify.backend.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.certify.backend.modelo.TipoDocumento;

public interface TipoDocumentoRepositorio extends JpaRepository<TipoDocumento, Integer>{

}
