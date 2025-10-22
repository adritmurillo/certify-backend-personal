package com.certify.backend.controlador;

import com.certify.backend.dto.PeticionCrearEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.servicio.EmpresaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Controlador que gestiona las operaciones sobre empresas. */
@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaControlador {

    private final EmpresaServicio empresaServicio;

    /** Crea una nueva empresa (solo para usuarios con rol ADMIN). */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RespuestaEmpresa> crearEmpresa(@Valid @RequestBody PeticionCrearEmpresa peticion) {
        RespuestaEmpresa nuevaEmpresa = empresaServicio.crearEmpresa(peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
    }
}
