package com.certify.backend.servicio;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import com.certify.backend.modelo.TipoDocumento;
import com.certify.backend.repositorio.TipoDocumentoRepositorio;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipoDocumentoService implements CRUDService<TipoDocumento, Integer>{

	@Autowired
	private TipoDocumentoRepositorio repositorio;
	
	@Override
	public TipoDocumento save(TipoDocumento entity) {
		// TODO Auto-generated method stub
		return repositorio.save(entity);
	}

	@Override
	public TipoDocumento saveAndFlush(TipoDocumento entity) {
		// TODO Auto-generated method stub
		return repositorio.saveAndFlush(entity);
	}

	@Override
	public List<TipoDocumento> findAll() {
		// TODO Auto-generated method stub
		return repositorio.findAll();
	}

	@Override
	public List<TipoDocumento> saveAll(List<TipoDocumento> entities) {
		// TODO Auto-generated method stub
		return repositorio.saveAll(entities);
	}

	@Override
	public Optional<TipoDocumento> findById(Integer id) {
		// TODO Auto-generated method stub
		return repositorio.findById(id);
	}

}
