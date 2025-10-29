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
        System.out.println("\n--- DEBUG: Iniciando solicitarRegistro ---");
        // ... (Validaciones de RUC y correo se mantienen)
        empresaRepositorio.findByRuc(peticion.getRuc()).ifPresent(e -> {
            throw new IllegalArgumentException("Ya existe una empresa con el RUC: " + peticion.getRuc());
        });
        usuarioRepositorio.findByCorreo(peticion.getCorreoAdmin()).ifPresent(u -> {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + peticion.getCorreoAdmin());
        });

        // ... (Obtención de entidades base se mantiene)
        EstadoRegistro estadoPendiente = estadoRegistroRepositorio.findByNombre("Pendiente")
                .orElseThrow(() -> new RuntimeException("Error: Estado 'Pendiente' no encontrado."));
        Rol rolAdminEmpresa = rolRepositorio.findByNombre("Admin Empresa")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Admin Empresa' no encontrado."));
        TipoDocumento tipoDNI = tipoDocumentoRepositorio.findByDescripcion("DNI")
                .orElseThrow(() -> new RuntimeException("Error: Tipo de documento 'DNI' no encontrado."));

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
        System.out.println("DEBUG: Creado objeto Empresa en memoria: " + nuevaEmpresa.getRazonSocial());
        System.out.println("DEBUG: Creado objeto Usuario en memoria: " + adminUsuario.getCorreo());
        System.out.println("DEBUG: Asignando usuario a la empresa...");

        // ========== CORRECCIÓN #1: Asegurar la relación bidireccional ==========
        // Esto garantiza que la conexión se guarde correctamente en la base de datos.
        nuevaEmpresa.setUsuarios(List.of(adminUsuario));

        // Verificamos la relación antes de guardar
        if (nuevaEmpresa.getUsuarios() != null && !nuevaEmpresa.getUsuarios().isEmpty()) {
            System.out.println("DEBUG: ¡ÉXITO! La lista de usuarios en la empresa NO está vacía. Contiene: " + nuevaEmpresa.getUsuarios().get(0).getCorreo());
        } else {
            System.err.println("DEBUG: ¡ERROR! La lista de usuarios en la empresa ESTÁ vacía ANTES de guardar.");
        }

        System.out.println("DEBUG: Guardando la empresa en la base de datos...");
        Empresa empresaGuardada = empresaRepositorio.save(nuevaEmpresa);
        System.out.println("--- DEBUG: Finalizado solicitarRegistro para empresa ID: " + empresaGuardada.getEmpresaId() + " ---\n");

        return mapearARespuesta(empresaGuardada);
    }

    @Override
    @Transactional
    public RespuestaEmpresa aprobarSolicitud(Integer empresaId) {
        System.out.println("\n--- DEBUG: Iniciando aprobarSolicitud para Empresa ID: " + empresaId + " ---");
        Empresa empresaPendiente = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        System.out.println("DEBUG: Empresa encontrada: " + empresaPendiente.getRazonSocial());

        if (empresaPendiente.getUsuarios() != null && !empresaPendiente.getUsuarios().isEmpty()) {
            System.out.println("DEBUG: ¡ÉXITO! La lista de usuarios de la empresa NO está vacía. Contiene " + empresaPendiente.getUsuarios().size() + " usuario(s).");
            System.out.println("DEBUG: Usuario en la lista: " + empresaPendiente.getUsuarios().get(0).getCorreo());
        } else {
            // Si ves este mensaje, aquí está el núcleo del problema.
            System.err.println("DEBUG: ¡ERROR! La lista de usuarios de la empresa ESTÁ vacía o es nula después de recuperarla de la BD.");
        }

        if (!"Pendiente".equals(empresaPendiente.getEstado().getNombre())) {
            throw new IllegalStateException("Solo se pueden aprobar empresas en estado 'Pendiente'");
        }

        // ========== CORRECCIÓN #2: Usar la relación del objeto en lugar del repositorio ==========
        // Esta es la forma más segura y correcta de encontrar el usuario asociado.
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

    // --- El resto de tus métodos CRUD que ya estaban bien ---

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
        cambiarEstado(empresaId, "Inactivo");
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