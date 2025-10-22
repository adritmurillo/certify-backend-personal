package com.certify.backend.controlador;

import com.certify.backend.dto.PeticionLogin;
import com.certify.backend.dto.RespuestaAutenticacion;
import com.certify.backend.servicio.AutenticacionServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que gestiona la autenticación de usuarios (login, registro, etc.).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacionControlador {

    private final AutenticacionServicio autenticacionServicio;

    /** Inicia sesión y devuelve un token JWT si las credenciales son válidas. */
    @PostMapping("/login")
    public ResponseEntity<RespuestaAutenticacion> login(@RequestBody PeticionLogin peticion) {
        return ResponseEntity.ok(autenticacionServicio.login(peticion));
    }

    // Futuro: endpoints para registro, logout, etc.
}
