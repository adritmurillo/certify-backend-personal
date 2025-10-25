package com.certify.backend.servicio;

import java.util.List;
import java.util.Optional;

import java.io.IOException;

public interface CRUDService<E, ID> {

    E save(E entity);
    E saveAndFlush(E entity);
    List<E> findAll();
    List<E> saveAll(List<E> entities);
    Optional<E> findById(ID id);
}
