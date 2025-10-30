package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionLogin;
import com.certify.backend.dto.RespuestaAutenticacion;


public interface AutenticacionServicio {


     // Autentica a un usuario en el sistema utilizando sus credenciales.

    RespuestaAutenticacion login(PeticionLogin peticion);

    // En el futuro se podrían añadir métodos adicionales, como el registro de nuevos usuarios,
    // refresco de tokens y cierre de sesión.
}
