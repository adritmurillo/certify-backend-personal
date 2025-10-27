package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionSolicitudEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.modelo.*;
import com.certify.backend.repositorio.*; // Asegúrate de importar todos los repositorios necesarios
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaServicioImpl implements EmpresaServicio {

    private final EmpresaRepositorio empresaRepositorio;
    private final EstadoRegistroRepositorio estadoRegistroRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio; // Necesario para el nuevo flujo
    private final TipoDocumentoRepositorio tipoDocumentoRepositorio; // Necesario para el nuevo flujo

    // --- NUEVOS MÉTODOS PARA EL FLUJO DE SOLICITUD Y APROBACIÓN ---

    @Override
    @Transactional
    public RespuestaEmpresa solicitarRegistro(PeticionSolicitudEmpresa peticion) {
        // 1. Validar duplicados (RUC y correo del admin)
        empresaRepositorio.findByRuc(peticion.getRuc()).ifPresent(e -> {
            throw new IllegalArgumentException("Ya existe una empresa con el RUC: " + peticion.getRuc());
        });
        usuarioRepositorio.findByCorreo(peticion.getCorreoAdmin()).ifPresent(u -> {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + peticion.getCorreoAdmin());
        });

        // 2. Obtener entidades base
        EstadoRegistro estadoPendiente = estadoRegistroRepositorio.findByNombre("Pendiente")
                .orElseThrow(() -> new RuntimeException("Error: Estado 'Pendiente' no encontrado."));
        Rol rolAdminEmpresa = rolRepositorio.findByNombre("Admin Empresa")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Admin Empresa' no encontrado."));
        TipoDocumento tipoDNI = tipoDocumentoRepositorio.findByDescripcion("DNI")
                .orElseThrow(() -> new RuntimeException("Error: Tipo de documento 'DNI' no encontrado."));

        // 3. Crear la nueva empresa (aún sin usuario creador)
        Empresa nuevaEmpresa = Empresa.builder()
                .ruc(peticion.getRuc())
                .razonSocial(peticion.getRazonSocial())
                .correoContacto(peticion.getCorreoContacto())
                .estado(estadoPendiente)
                .build();

        // 4. Crear la persona y el usuario administrador para esa empresa
        Persona adminPersona = Persona.builder()
                .nombres(peticion.getNombresAdmin())
                .apellidos(peticion.getApellidosAdmin())
                .documento(peticion.getDocumentoAdmin())
                .tipoDocumento(tipoDNI)
                .build();

        Usuario adminUsuario = Usuario.builder()
                .persona(adminPersona) // Se guardará en cascada
                .correo(peticion.getCorreoAdmin())
                .rol(rolAdminEmpresa)
                .empresa(nuevaEmpresa) // Vinculamos el usuario a la empresa
                // Sin contraseña por ahora, hasta el flujo de activación
                .build();

        // 5. Guardar la empresa (la cascada guardará la persona y el usuario)
        // OJO: Necesitaremos añadir la cascada en la entidad Empresa
        Empresa empresaGuardada = empresaRepositorio.save(nuevaEmpresa);

        return mapearARespuesta(empresaGuardada);
    }

    @Override
    @Transactional
    public RespuestaEmpresa aprobarSolicitud(Integer empresaId) {
        // 1. Obtener la empresa y el estado "Activo"
        Empresa empresaPendiente = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));

        EstadoRegistro estadoActivo = estadoRegistroRepositorio.findByNombre("Activa")
                .orElseThrow(() -> new RuntimeException("Error: Estado 'Activo' no encontrado."));

        // 2. Actualizar el estado y guardar
        empresaPendiente.setEstado(estadoActivo);
        Empresa empresaAprobada = empresaRepositorio.save(empresaPendiente);

        // Aquí iría la lógica para un posible envio de activacion de cuenta mediante gmail

        return mapearARespuesta(empresaAprobada);
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---

    private Usuario obtenerUsuarioAutenticado() {
        String correoSuperAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByCorreo(correoSuperAdmin)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el contexto de seguridad."));
    }

    private RespuestaEmpresa mapearARespuesta(Empresa empresa) {
        return RespuestaEmpresa.builder()
                .empresaId(empresa.getEmpresaId())
                .ruc(empresa.getRuc())
                .razonSocial(empresa.getRazonSocial())
                .correoContacto(empresa.getCorreoContacto())
                .logoUrl(empresa.getLogoUrl())
                .estadoNombre(empresa.getEstado() != null ? empresa.getEstado().getNombre() : "N/A")
                .fechaCreacion(empresa.getFechaCreacion())
                .build();
    }
    @Override
    public List<RespuestaEmpresa> listarTodas() {
        return empresaRepositorio.findAll()
                .stream()
                .map(this::mapearARespuesta) // Reutilizamos nuestro método de mapeo
                .toList();
    }
}