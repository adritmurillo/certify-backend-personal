package com.certify.backend;

import com.certify.backend.modelo.*;
import com.certify.backend.repositorio.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Clase principal del backend de Certify.
 *
 * Inicia la aplicación Spring Boot y configura la creación automática
 * de datos de prueba al iniciar.
 */
@SpringBootApplication
public class CertifyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertifyBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            UsuarioRepositorio usuarioRepositorio,
            RolRepositorio rolRepositorio,
            PersonaRepositorio personaRepositorio,
            EstadoRegistroRepositorio estadoRegistroRepositorio,
            TipoDocumentoRepositorio tipoDocumentoRepositorio,
            EmpresaRepositorio empresaRepositorio,
            // Manteniendo la inyección que necesitas
            EventoCursoRepositorio eventoCursoRepositorio,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (rolRepositorio.count() == 0) {
                System.out.println("Creando datos de prueba iniciales...");

                // --- 1. CREAR DATOS BASE ---
                Rol rolAdmin = Rol.builder().nombre("ADMIN").build();
                Rol rolAdminEmpresa = Rol.builder().nombre("Admin Empresa").build();
                rolRepositorio.saveAll(List.of(rolAdmin, rolAdminEmpresa));

                // --- SECCIÓN CORREGIDA: Integrando los 4 estados ---
                EstadoRegistro estadoActivo = EstadoRegistro.builder().nombre("Activa").build();
                EstadoRegistro estadoInactivo = EstadoRegistro.builder().nombre("Inactivo").build();
                EstadoRegistro estadoPendiente = EstadoRegistro.builder().nombre("Pendiente").build();
                EstadoRegistro estadoRechazado = EstadoRegistro.builder().nombre("Rechazado").build();
                EstadoRegistro estadoArchivado = EstadoRegistro.builder().nombre("Archivado").build();
                estadoRegistroRepositorio.saveAll(List.of(estadoActivo, estadoInactivo, estadoPendiente, estadoRechazado, estadoArchivado));


                TipoDocumento tipoDNI = TipoDocumento.builder().descripcion("DNI").build();
                tipoDocumentoRepositorio.save(tipoDNI);

                // --- 2. CREAR SUPERADMIN ---
                Persona personaAdmin = Persona.builder().nombres("Super").apellidos("Admin").documento("00000001").tipoDocumento(tipoDNI).build();
                Usuario usuarioAdmin = Usuario.builder().persona(personaAdmin).rol(rolAdmin).correo("admin@certify.com").contrasena(passwordEncoder.encode("123456")).build();
                usuarioRepositorio.save(usuarioAdmin);
                System.out.println("✅ Usuario Superadmin de prueba creado: admin@certify.com / 123456");

                // --- 3. CREAR EMPRESA DE PRUEBA Y USUARIO ASOCIADO ---
                Empresa empresaPrueba = Empresa.builder().ruc("12345678901").razonSocial("Empresa de Prueba").correoContacto("contacto@prueba.com").estado(estadoActivo).creadoPor(usuarioAdmin).build();
                empresaRepositorio.save(empresaPrueba);

                Persona personaEmpresa = Persona.builder().nombres("Usuario").apellidos("Empresa").documento("11111111").tipoDocumento(tipoDNI).build();
                Usuario usuarioEmpresa = Usuario.builder().persona(personaEmpresa).rol(rolAdminEmpresa).empresa(empresaPrueba).correo("empresa@certify.com").contrasena(passwordEncoder.encode("123456")).build();
                usuarioRepositorio.save(usuarioEmpresa);
                System.out.println("✅ Usuario Admin Empresa de prueba creado: empresa@certify.com / 123456");

                // --- 4. CREAR ÁREAS/PROYECTOS DE PRUEBA (EVENTO CURSO) ---
                // Se mantiene tu bloque de código intacto
                System.out.println("Creando Áreas/Proyectos de prueba...");
                EventoCurso dev = EventoCurso.builder().nombre("Desarrollo de Software").estado(estadoActivo).empresa(empresaPrueba).creadoPor(usuarioEmpresa).build();
                EventoCurso it = EventoCurso.builder().nombre("Soporte de TI").estado(estadoActivo).empresa(empresaPrueba).creadoPor(usuarioEmpresa).build();
                EventoCurso data = EventoCurso.builder().nombre("Análisis de Datos").estado(estadoActivo).empresa(empresaPrueba).creadoPor(usuarioEmpresa).build();

                eventoCursoRepositorio.saveAll(List.of(dev, it, data));
                System.out.println("✅ Áreas/Proyectos de prueba creados.");
            }
        };
    }
}