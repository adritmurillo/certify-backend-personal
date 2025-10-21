package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionLogin;
import com.certify.backend.dto.RespuestaAutenticacion;

public interface AutenticacionServicio {
    // Autenticamos a un usuario basado en sus credenciales
    // @param peticion Los datos de login (correo y contraseña)
    // @return Una respuesta que contiene el token JWT si la autenticación es exitosa

    RespuestaAutenticacion login(PeticionLogin peticion);

    // Se pueden añadir mas metodos en el futuro el de registrar;
}
