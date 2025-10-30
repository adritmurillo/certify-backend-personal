package com.certify.backend.repositorio;

import com.certify.backend.modelo.Empresa;
import com.certify.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 *
 * Permite realizar operaciones CRUD sobre la tabla "usuario"
 * y facilita la búsqueda de usuarios por campos específicos.
 */
@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByEmpresa(Empresa empresa);
}
