package com.certify.backend.controlador;

import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.servicio.EmpresaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaControlador {

    private final EmpresaServicio empresaServicio;

    /**
     * Obtiene una lista de todas las empresas.
     * Solo para usuarios con rol ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RespuestaEmpresa>> listarEmpresas() {
        // Necesitaremos añadir este método al servicio
        List<RespuestaEmpresa> empresas = empresaServicio.listarTodas();
        return ResponseEntity.ok(empresas);
    }

    /**
     * Aprueba una solicitud de empresa pendiente.
     * Solo para usuarios con rol ADMIN.
     */
    @PatchMapping("/{empresaId}/aprobar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RespuestaEmpresa> aprobarEmpresa(
            @PathVariable Integer empresaId
    ) {
        RespuestaEmpresa empresaAprobada = empresaServicio.aprobarSolicitud(empresaId);
        return ResponseEntity.ok(empresaAprobada);
    }
}