package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionLogin;
import com.certify.backend.dto.RespuestaAutenticacion;

/**
 * Servicio encargado de la lógica de autenticación de usuarios.
 *
 * Define los métodos que permiten validar las credenciales de acceso
 * y generar tokens JWT para el inicio de sesión.
 */
public interface AutenticacionServicio {

    /**
     * Autentica a un usuario en el sistema utilizando sus credenciales.
     *
     * Si las credenciales son válidas, se genera un token JWT
     * que el cliente podrá usar para futuras peticiones autenticadas.
     *
     * @param peticion objeto con los datos de inicio de sesión (correo y contraseña)
     * @return objeto {@link RespuestaAutenticacion} con el token JWT generado
     */
    RespuestaAutenticacion login(PeticionLogin peticion);

    // En el futuro se podrían añadir métodos adicionales, como:
    // - registro de nuevos usuarios
    // - refresco de tokens
    // - cierre de sesión
}
