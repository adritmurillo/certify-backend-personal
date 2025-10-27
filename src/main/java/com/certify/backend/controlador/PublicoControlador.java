package com.certify.backend.controlador;

import com.certify.backend.dto.PeticionSolicitudEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.servicio.EmpresaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/publico")
@RequiredArgsConstructor
public class PublicoControlador {

    private final EmpresaServicio empresaServicio;

    @PostMapping("/solicitud-empresa")
    public ResponseEntity<RespuestaEmpresa> solicitarRegistro(
            @Valid @RequestBody PeticionSolicitudEmpresa peticion
    ) {
        RespuestaEmpresa empresaCreada = empresaServicio.solicitarRegistro(peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaCreada);
    }
}