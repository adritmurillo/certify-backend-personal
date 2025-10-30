package com.certify.backend.controlador;

import com.certify.backend.dto.PeticionActualizarParticipante;
import com.certify.backend.dto.PeticionCrearParticipante;
import com.certify.backend.dto.RespuestaParticipante;
import com.certify.backend.servicio.ParticipanteServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/participantes")
@RequiredArgsConstructor
// --- MODIFICACIÓN DE SEGURIDAD ---
// Permitimos que tanto el ADMIN como el Admin Empresa puedan gestionar practicantes.
@PreAuthorize("hasAnyAuthority('ADMIN', 'Admin Empresa')")
public class ParticipanteControlador {

    private final ParticipanteServicio participanteServicio;

    @PostMapping
    public ResponseEntity<RespuestaParticipante> crearParticipante(@Valid @RequestBody PeticionCrearParticipante peticion) {
        RespuestaParticipante nuevoParticipante = participanteServicio.crearParticipante(peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoParticipante);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaParticipante>> obtenerTodos(
            @RequestParam(required = false) String nombreODni,
            @RequestParam(required = false) Integer areaProyectoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {

        List<RespuestaParticipante> participantes = participanteServicio.obtenerPracticantesConFiltros(
                nombreODni, areaProyectoId, fechaInicio, fechaFin
        );
        return ResponseEntity.ok(participantes);
    }

    // --- READ (Obtener un solo practicante por su ID) ---
    // GET /api/v1/participantes/1
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaParticipante> obtenerPorId(@PathVariable Integer id) {
        RespuestaParticipante participante = participanteServicio.obtenerPorId(id);
        return ResponseEntity.ok(participante);
    }

    // --- UPDATE (Actualizar un practicante existente) ---
    // PUT /api/v1/participantes/1
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaParticipante> actualizarParticipante(
            @PathVariable Integer id,
            @Valid @RequestBody PeticionActualizarParticipante peticion
    ) {
        RespuestaParticipante participanteActualizado = participanteServicio.actualizarParticipante(id, peticion);
        return ResponseEntity.ok(participanteActualizado);
    }

    // --- CHANGE STATUS (Para el ícono del ojo: Activo/Inactivo) ---
    // PATCH /api/v1/participantes/1/estado?nuevoEstado=Inactivo
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RespuestaParticipante> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado
    ) {
        RespuestaParticipante participante = participanteServicio.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(participante);
    }

    // --- DELETE (Soft Delete: Para el ícono de la papelera) ---
    // DELETE /api/v1/participantes/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarParticipante(@PathVariable Integer id) {
        participanteServicio.eliminarParticipante(id);
        return ResponseEntity.noContent().build();
    }
}