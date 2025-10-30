package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionActualizarEmpresa;
import com.certify.backend.dto.PeticionSolicitudEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.modelo.*;
import com.certify.backend.repositorio.*;
import com.certify.backend.util.GeneradorContrasena;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // Asegúrate de tener estos imports
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Asegúrate de importar Optional

@Service
@RequiredArgsConstructor
public class EmpresaServicioImpl implements EmpresaServicio {

    private final EmpresaRepositorio empresaRepositorio;
    private final EstadoRegistroRepositorio estadoRegistroRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final TipoDocumentoRepositorio tipoDocumentoRepositorio;
    private final EmailServicio emailServicio;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RespuestaEmpresa solicitarRegistro(PeticionSolicitudEmpresa peticion) {
        System.out.println("\n--- DEBUG: Iniciando solicitarRegistro para RUC: " + peticion.getRuc() + " ---");

        // --- PASO 1: Buscar por RUC ---
        Optional<Empresa> empresaExistenteOpt = empresaRepositorio.findByRuc(peticion.getRuc());

        // --- PASO 2: Si el RUC existe, manejar los diferentes estados ---
        if (empresaExistenteOpt.isPresent()) {
            Empresa empresa = empresaExistenteOpt.get();
            String estadoActual = empresa.getEstado().getNombre();

            switch (estadoActual) {
                case "Rechazado":
                    // --- CASO A: Lógica de Reactivación (Empresa Rechazada) ---
                    System.out.println("DEBUG: RUC encontrado con estado 'Rechazado'. Iniciando reactivación.");

                    // Obtener el usuario admin de esta empresa
                    Usuario adminUsuario = empresa.getUsuarios().stream()
                            .filter(u -> "Admin Empresa".equals(u.getRol().getNombre()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Error crítico: Empresa rechazada no tiene usuario admin. ID: " + empresa.getEmpresaId()));

                    // --- Validación de Correo (el "Agujero de Seguridad") ---
                    Optional<Usuario> correoExistenteOpt = usuarioRepositorio.findByCorreo(peticion.getCorreoAdmin());
                    if (correoExistenteOpt.isPresent() && !correoExistenteOpt.get().getUsuarioId().equals(adminUsuario.getUsuarioId())) {
                        // El correo existe y NO pertenece a este usuario.
                        throw new IllegalArgumentException("El correo " + peticion.getCorreoAdmin() + " ya está en uso por otro usuario.");
                    }
                    // --- Fin de Validación de Correo ---

                    EstadoRegistro estadoPendiente = estadoRegistroRepositorio.findByNombre("Pendiente")
                            .orElseThrow(() -> new RuntimeException("Error: Estado 'Pendiente' no encontrado."));

                    // Actualizar datos de la Empresa
                    empresa.setRazonSocial(peticion.getRazonSocial());
                    empresa.setCorreoContacto(peticion.getCorreoContacto());
                    empresa.setEstado(estadoPendiente);
                    empresa.setMotivoRechazo(null); // Limpiar motivo anterior

                    // Actualizar datos del Usuario y Persona
                    adminUsuario.setCorreo(peticion.getCorreoAdmin());
                    Persona adminPersona = adminUsuario.getPersona();
                    adminPersona.setNombres(peticion.getNombresAdmin());
                    adminPersona.setApellidos(peticion.getApellidosAdmin());
                    adminPersona.setDocumento(peticion.getDocumentoAdmin());

                    System.out.println("DEBUG: Datos actualizados. Guardando empresa ID: " + empresa.getEmpresaId());
                    Empresa empresaActualizada = empresaRepositorio.save(empresa);
                    return mapearARespuesta(empresaActualizada);

                case "Activa":
                case "Pendiente":
                    // --- CASO B: Bloquear si ya está activa o pendiente ---
                    System.err.println("DEBUG: RUC encontrado con estado '" + estadoActual + "'. Solicitud bloqueada.");
                    throw new IllegalArgumentException("Ya existe una empresa activa o pendiente con el RUC: " + peticion.getRuc());

                default:
                    // --- CASO C: Manejar otros estados (ej. "Archivado") ---
                    System.err.println("DEBUG: RUC encontrado con estado '" + estadoActual + "'. Solicitud bloqueada.");
                    throw new IllegalStateException("El estado de la empresa (" + estadoActual + ") no permite una nueva solicitud.");
            }
        }

        // --- PASO 3: Si el RUC no existe, es un registro nuevo ---
        System.out.println("DEBUG: RUC no encontrado. Procediendo con nuevo registro.");

        // Validar que el correo del nuevo admin no exista
        usuarioRepositorio.findByCorreo(peticion.getCorreoAdmin()).ifPresent(u -> {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + peticion.getCorreoAdmin());
        });

        // Obtener entidades base
        EstadoRegistro estadoPendiente = estadoRegistroRepositorio.findByNombre("Pendiente")
                .orElseThrow(() -> new RuntimeException("Error: Estado 'Pendiente' no encontrado."));
        Rol rolAdminEmpresa = rolRepositorio.findByNombre("Admin Empresa")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Admin Empresa' no encontrado."));
        TipoDocumento tipoDNI = tipoDocumentoRepositorio.findByDescripcion("DNI")
                .orElseThrow(() -> new RuntimeException("Error: Tipo de documento 'DNI' no encontrado."));

        // Lógica de creación original
        Empresa nuevaEmpresa = Empresa.builder()
                .ruc(peticion.getRuc())
                .razonSocial(peticion.getRazonSocial())
                .correoContacto(peticion.getCorreoContacto())
                .estado(estadoPendiente)
                .build();

        Persona adminPersona = Persona.builder()
                .nombres(peticion.getNombresAdmin())
                .apellidos(peticion.getApellidosAdmin())
                .documento(peticion.getDocumentoAdmin())
                .tipoDocumento(tipoDNI)
                .build();

        Usuario adminUsuario = Usuario.builder()
                .persona(adminPersona)
                .correo(peticion.getCorreoAdmin())
                .rol(rolAdminEmpresa)
                .empresa(nuevaEmpresa)
                .build();

        nuevaEmpresa.setUsuarios(List.of(adminUsuario));

        System.out.println("DEBUG: Guardando nueva empresa...");
        Empresa empresaGuardada = empresaRepositorio.save(nuevaEmpresa);
        System.out.println("--- DEBUG: Finalizado nuevo registro para empresa ID: " + empresaGuardada.getEmpresaId() + " ---\n");

        return mapearARespuesta(empresaGuardada);
    }


    @Override
    @Transactional
    public RespuestaEmpresa aprobarSolicitud(Integer empresaId) {
        System.out.println("\n--- DEBUG: Iniciando aprobarSolicitud para Empresa ID: " + empresaId + " ---");
        Empresa empresaPendiente = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        System.out.println("DEBUG: Empresa encontrada: " + empresaPendiente.getRazonSocial());

        if (empresaPendiente.getUsuarios() == null || empresaPendiente.getUsuarios().isEmpty()) {
            System.err.println("DEBUG: ¡ERROR! La lista de usuarios de la empresa ESTÁ vacía o es nula después de recuperarla de la BD.");
            throw new IllegalStateException("Error crítico: No se encontró usuario asociado a la empresa.");
        }

        if (!"Pendiente".equals(empresaPendiente.getEstado().getNombre())) {
            throw new IllegalStateException("Solo se pueden aprobar empresas en estado 'Pendiente'");
        }

        Usuario adminEmpresa = empresaPendiente.getUsuarios().stream()
                .filter(u -> "Admin Empresa".equals(u.getRol().getNombre()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario administrador para la empresa"));

        String contrasenaTemporal = GeneradorContrasena.generarTemporal();
        adminEmpresa.setContrasena(passwordEncoder.encode(contrasenaTemporal));

        EstadoRegistro estadoActivo = estadoRegistroRepositorio.findByNombre("Activa")
                .orElseThrow(() -> new RuntimeException("Estado 'Activa' no encontrado."));

        empresaPendiente.setEstado(estadoActivo);
        Empresa empresaAprobada = empresaRepositorio.save(empresaPendiente);

        System.out.println("DEBUG: Empresa aprobada y guardada. Procediendo a enviar email...");

        try {
            emailServicio.enviarCredencialesTemporales(
                    adminEmpresa.getCorreo(),
                    adminEmpresa.getCorreo(),
                    contrasenaTemporal
            );
            System.out.println("DEBUG: Email enviado exitosamente a " + adminEmpresa.getCorreo());
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar email: " + e.getMessage());
        }

        System.out.println("--- DEBUG: Finalizado aprobarSolicitud --- \n");
        return mapearARespuesta(empresaAprobada);
    }

    // --- NUEVO MÉTODO DE RECHAZO ---
    @Override
    @Transactional
    public RespuestaEmpresa rechazarSolicitud(Integer empresaId, String motivo) {
        Empresa empresaPendiente = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));

        if (!"Pendiente".equals(empresaPendiente.getEstado().getNombre())) {
            throw new IllegalStateException("Solo se pueden rechazar empresas en estado 'Pendiente'");
        }

        EstadoRegistro estadoRechazado = estadoRegistroRepositorio.findByNombre("Rechazado")
                .orElseThrow(() -> new RuntimeException("Estado 'Rechazado' no encontrado."));

        empresaPendiente.setEstado(estadoRechazado);
        empresaPendiente.setMotivoRechazo(motivo); // Guardamos el motivo

        Empresa empresaRechazada = empresaRepositorio.save(empresaPendiente);

        // Notificar por correo
        try {
            // Asumimos que el primer usuario de la lista es el admin que solicitó el registro
            Usuario adminSolicitante = empresaRechazada.getUsuarios().stream()
                    .filter(u -> "Admin Empresa".equals(u.getRol().getNombre()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se encontró admin para notificar rechazo."));

            emailServicio.enviarNotificacionRechazo(adminSolicitante.getCorreo(), motivo);
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar email de rechazo: " + e.getMessage());
        }

        return mapearARespuesta(empresaRechazada);
    }


    @Override
    public List<RespuestaEmpresa> listarTodas() {
        return empresaRepositorio.findAll().stream().map(this::mapearARespuesta).toList();
    }

    @Override
    public RespuestaEmpresa obtenerPorId(Integer empresaId) {
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        return mapearARespuesta(empresa);
    }

    @Override
    public List<RespuestaEmpresa> listarConFiltros(String nombre, String estado) {
        return empresaRepositorio.buscarConFiltros(nombre, estado)
                .stream().map(this::mapearARespuesta).toList();
    }

    @Override
    @Transactional
    public RespuestaEmpresa actualizarEmpresa(Integer empresaId, PeticionActualizarEmpresa peticion) {
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        empresa.setRazonSocial(peticion.getRazonSocial());
        empresa.setCorreoContacto(peticion.getCorreoContacto());
        return mapearARespuesta(empresaRepositorio.save(empresa));
    }

    @Override
    @Transactional
    public RespuestaEmpresa cambiarEstado(Integer empresaId, String nuevoEstado) {
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        EstadoRegistro estado = estadoRegistroRepositorio.findByNombre(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + nuevoEstado));
        empresa.setEstado(estado);
        return mapearARespuesta(empresaRepositorio.save(empresa));
    }

    @Override
    @Transactional
    public void eliminarEmpresa(Integer empresaId) {
        // Esta lógica necesita ser actualizada a "Archivado"
        // cambiarEstado(empresaId, "Inactivo"); // <- Esta línea está rota

        // --- LÓGICA DE SOFT DELETE CORRECTA ---
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));

        EstadoRegistro estadoArchivado = estadoRegistroRepositorio.findByNombre("Archivado")
                .orElseThrow(() -> new RuntimeException("Estado 'Archivado' no encontrado."));

        empresa.setEstado(estadoArchivado);
        empresaRepositorio.save(empresa);
    }
    // METODO PARA ACTUALIZAR CORREO DEL ADMIN DE LA EMPRESA EN CASO ESTE LO HAYA PERDIDO
    @Override
    @Transactional
    public void actualizarCorreoAdmin(Integer empresaId, String nuevoCorreo) {
        // 1. Validar que el nuevo correo no esté ya en uso por otro usuario
        usuarioRepositorio.findByCorreo(nuevoCorreo).ifPresent(u -> {
            throw new IllegalArgumentException("El correo " + nuevoCorreo + " ya está registrado en el sistema.");
        });

        // 2. Encontrar la empresa
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));

        // 3. Encontrar al usuario "Admin Empresa" de esa empresa
        Usuario adminUsuario = empresa.getUsuarios().stream()
                .filter(u -> "Admin Empresa".equals(u.getRol().getNombre()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró un usuario administrador para la empresa con ID: " + empresaId));

        // 4. Actualizar el correo
        String correoAntiguo = adminUsuario.getCorreo();
        adminUsuario.setCorreo(nuevoCorreo);
        usuarioRepositorio.save(adminUsuario);
        System.out.println("DEBUG: Correo del admin de la empresa " + empresaId + " actualizado de " + correoAntiguo + " a " + nuevoCorreo);

        // 5. Reenviar credenciales (aquí generamos una nueva contraseña temporal)
        try {
            String nuevaContrasenaTemporal = GeneradorContrasena.generarTemporal();
            adminUsuario.setContrasena(passwordEncoder.encode(nuevaContrasenaTemporal));
            usuarioRepositorio.save(adminUsuario);

            emailServicio.enviarCredencialesTemporales(
                    nuevoCorreo,
                    nuevoCorreo,
                    nuevaContrasenaTemporal
            );
            System.out.println("DEBUG: Se han reenviado credenciales al nuevo correo: " + nuevoCorreo);
        } catch (Exception e) {
            System.err.println("⚠️ Error al reenviar credenciales: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void eliminarSolicitudRechazada(Integer empresaId) {
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));

        // --- ¡VALIDACIÓN DE SEGURIDAD CLAVE! ---
        if (!"Rechazado".equals(empresa.getEstado().getNombre())) {
            throw new IllegalStateException("Solo se pueden eliminar permanentemente las solicitudes que han sido rechazadas.");
        }

        empresaRepositorio.delete(empresa);
        System.out.println("DEBUG: Se ha eliminado permanentemente la solicitud de la empresa con ID: " + empresaId);
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
}
