package com.certify.backend.controlador;

import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.servicio.ParticipanteServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/participantes")
@RequiredArgsConstructor

public class ParticipanteControlador {
    private final ParticipanteServicio participanteServicio;
    @PostMapping
    @PreAuthorize("hasAuthority('Admin Empresa')")
    public ResponseEntity<RespuestaParticipante> crearParticipante(@Valid @RequestBody PeticionCrearParticipante peticion){
        RespuestaParticipante nuevoParticipante = participanteServicio.crearParticipante(peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoParticipante);
    }
}