package com.certify.backend.controlador;


import com.certify.backend.dto.PeticionLogin;
import com.certify.backend.dto.RespuestaAutenticacion;
import com.certify.backend.servicio.AutenticacionServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacionControlador {
    private final AutenticacionServicio autenticacionServicio;
    @PostMapping("/login")
    public ResponseEntity<RespuestaAutenticacion> login(
            @RequestBody PeticionLogin peticion
    ) {
        return ResponseEntity.ok(autenticacionServicio.login(peticion));
    }
    // Aqui mas abajo iran mas cosas como logout, registrar, etc.
}
