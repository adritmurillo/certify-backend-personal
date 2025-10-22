package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionCrearEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;
import com.certify.backend.modelo.Empresa;
import com.certify.backend.modelo.EstadoRegistro;
import com.certify.backend.modelo.Usuario;
import com.certify.backend.repositorio.EmpresaRepositorio;
import com.certify.backend.repositorio.EstadoRegistroRepositorio;
import com.certify.backend.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio encargado de gestionar la lógica
 * relacionada con la creación y administración de empresas.
 *
 * Este servicio:
 *  - Evita la creación de empresas con RUC duplicado.
 *  - Asigna automáticamente el estado "Activa" al crear una empresa.
 *  - Registra qué usuario (superadmin) realizó la creación.
 */
@Service
@RequiredArgsConstructor
public class EmpresaServicioImpl implements EmpresaServicio {

    private final EmpresaRepositorio empresaRepositorio;
    private final EstadoRegistroRepositorio estadoRegistroRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    /**
     * Crea una nueva empresa validando las reglas de negocio:
     *  1. No permite RUC duplicado.
     *  2. Asigna estado "Activa" por defecto.
     *  3. Registra el usuario que la creó.
     *
     * @param peticion DTO con los datos necesarios para crear la empresa.
     * @return DTO con la información de la empresa creada.
     * @throws IllegalArgumentException si ya existe una empresa con el mismo RUC.
     * @throws RuntimeException si el estado "Activa" no existe en la base de datos.
     */
    @Override
    @Transactional
    public RespuestaEmpresa crearEmpresa(PeticionCrearEmpresa peticion) {

        // 1️. Verificar duplicado de RUC
        empresaRepositorio.findByRuc(peticion.getRuc()).ifPresent(empresa -> {
            throw new IllegalArgumentException("La empresa ya existe: " + peticion.getRuc());
        });

        // 2️. Obtener el estado "Activa"
        EstadoRegistro estadoActivo = estadoRegistroRepositorio.findByNombre("Activa")
                .orElseThrow(() -> new RuntimeException("Error: Estado 'Activa' no encontrado en la base de datos."));

        // 3️. Obtener el usuario autenticado (superadmin)
        Usuario superAdmin = obtenerUsuarioAutenticado();

        // 4️. Construir y guardar la nueva empresa
        Empresa nuevaEmpresa = Empresa.builder()
                .ruc(peticion.getRuc())
                .razonSocial(peticion.getRazonSocial())
                .correoContacto(peticion.getCorreoContacto())
                .estado(estadoActivo)
                .creadoPor(superAdmin)
                .build();

        Empresa empresaGuardada = empresaRepositorio.save(nuevaEmpresa);

        // 5️. Devolver la respuesta en formato DTO
        return new RespuestaEmpresa(
                empresaGuardada.getEmpresaId(),
                empresaGuardada.getRuc(),
                empresaGuardada.getRazonSocial(),
                empresaGuardada.getCorreoContacto(),
                empresaGuardada.getLogoUrl(),
                empresaGuardada.getEstado().getNombre(),
                empresaGuardada.getFechaCreacion()
        );
    }

    /**
     * Obtiene el usuario autenticado actualmente en el contexto de seguridad.
     * Spring Security almacena el correo del usuario en el Authentication principal.
     *
     * @return Usuario autenticado (superadmin).
     * @throws UsernameNotFoundException si el usuario no existe en la base de datos.
     */
    private Usuario obtenerUsuarioAutenticado() {
        String correoSuperAdmin = SecurityContextHolder.getContext().getAuthentication().getName();

        return usuarioRepositorio.findByCorreo(correoSuperAdmin)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el contexto de seguridad."));
    }
}
