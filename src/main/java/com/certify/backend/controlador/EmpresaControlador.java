package com.certify.backend.controlador;

import com.certify.backend.dto.PeticionActualizarCorreoAdmin;
import com.certify.backend.dto.PeticionActualizarEmpresa;
import com.certify.backend.dto.PeticionRechazarSolicitud;
import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.servicio.EmpresaServicio;
import jakarta.validation.Valid;
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

    // APROBAR SOLICITUD DE REGISTRO DE EMPRESA
    @PatchMapping("/{empresaId}/aprobar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RespuestaEmpresa> aprobarEmpresa(
            @PathVariable Integer empresaId
    ) {
        RespuestaEmpresa empresaAprobada = empresaServicio.aprobarSolicitud(empresaId);
        return ResponseEntity.ok(empresaAprobada);
    }

    // RECHAZAR LA SOLICITUD DE REGISTRO DE EMPRESA
    @PatchMapping("/{empresaId}/rechazar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RespuestaEmpresa> rechazarEmpresa(
            @PathVariable Integer empresaId,
            @Valid @RequestBody PeticionRechazarSolicitud peticion
    ) {
        RespuestaEmpresa empresaRechazada = empresaServicio.rechazarSolicitud(empresaId, peticion.getMotivo());
        return ResponseEntity.ok(empresaRechazada);
    }

    // ENDPOINT PARA RECUPERAR EL CORREO DE ADMIN DE LA EMPRESA Y ACTUALIZARLO

    @PatchMapping("/{empresaId}/actualizar-correo-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> actualizarCorreoAdmin(
            @PathVariable Integer empresaId,
            @Valid @RequestBody PeticionActualizarCorreoAdmin peticion
    ) {
        empresaServicio.actualizarCorreoAdmin(empresaId, peticion.getNuevoCorreo());
        return ResponseEntity.noContent().build();
    }


    // MAS ENDPOINTS PARA UN CRUD MAS COMPLETO

    // Eliminar empresa, soft delete (AHORA ARCHIVAR)
    @DeleteMapping("/{empresaId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Integer empresaId) {
        empresaServicio.eliminarEmpresa(empresaId);
        return ResponseEntity.noContent().build();
    }

    // --- AÑADIR ESTE NUEVO ENDPOINT PARA EL HARD DELETE ---
    @DeleteMapping("/{empresaId}/permanente")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarSolicitudPermanentemente(@PathVariable Integer empresaId) {
        empresaServicio.eliminarSolicitudRechazada(empresaId);
        return ResponseEntity.noContent().build();
    }

    // UPDATE PARA CAMBIAR DE ESTADO
    @PatchMapping("/{empresaId}/estado")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RespuestaEmpresa> cambiarEstado(
            @PathVariable Integer empresaId,
            @RequestParam String nuevoEstado
    ) {
        RespuestaEmpresa empresa = empresaServicio.cambiarEstado(empresaId, nuevoEstado);
        return ResponseEntity.ok(empresa);
    }

    // UPDATE PARA MODIFICAR DATOS DE LA EMPRESA (Excepto RUC)
    // Solo ADMIN Empresa puede modificar su propia empresa
    // SUPERADMIN PUEDE MODIFICAR CUALQUIER EMPRESA

    @PutMapping("/{empresaId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<RespuestaEmpresa> actualizarEmpresa(
            @PathVariable Integer empresaId,
            @Valid @RequestBody PeticionActualizarEmpresa peticion
    ) {
        RespuestaEmpresa empresaActualizada = empresaServicio.actualizarEmpresa(empresaId, peticion);
        return ResponseEntity.ok(empresaActualizada);
    }

    // LISTADO DE EMPRESAS CON FILTROS OPCIONALES
    // SUPERADMIN PUEDE VER TODAS LAS EMPRESAS CON FILTROS
    // ADMIN EMPRESA SOLO PUEDE VER SU PROPIA EMPRESA

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Admin Empresa')")
    public ResponseEntity<List<RespuestaEmpresa>> listarEmpresas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado
    ) {
        List<RespuestaEmpresa> empresas = empresaServicio.listarConFiltros(nombre, estado);
        return ResponseEntity.ok(empresas);
    }
}
