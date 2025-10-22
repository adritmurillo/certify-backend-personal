package com.certify.backend.repositorio;

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

    /**
     * Busca un usuario por su correo electrónico.
     *
     * Este método es utilizado principalmente por el servicio
     * de autenticación para cargar los datos del usuario
     * durante el proceso de inicio de sesión (login).
     *
     * @param correo correo electrónico del usuario.
     * @return un Optional que contiene el usuario si existe, o vacío si no se encuentra.
     */
    Optional<Usuario> findByCorreo(String correo);
}
