package com.certify.backend;

import com.certify.backend.modelo.EstadoRegistro;
import com.certify.backend.modelo.Persona;
import com.certify.backend.modelo.Rol;
import com.certify.backend.modelo.Usuario;
import com.certify.backend.repositorio.EstadoRegistroRepositorio;
import com.certify.backend.repositorio.PersonaRepositorio;
import com.certify.backend.repositorio.RolRepositorio;
import com.certify.backend.repositorio.UsuarioRepositorio;
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
 * de datos de prueba al iniciar, como el rol ADMIN, estados base
 * ("Activa", "Inactiva") y un usuario superadministrador.
 */
@SpringBootApplication
public class CertifyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertifyBackendApplication.class, args);
    }

    /**
     * Crea datos iniciales de prueba si la base de datos está vacía.
     *
     * Este método:
     *  - Crea el rol ADMIN (si no existe).
     *  - Crea los estados "Activa" e "Inactiva".
     *  - Crea un usuario administrador con correo "admin@certify.com" y contraseña "123456".
     *
     *  Solo se ejecuta una vez, al inicio, si no hay roles registrados.
     */
    @Bean
    public CommandLineRunner commandLineRunner(
            UsuarioRepositorio usuarioRepositorio,
            RolRepositorio rolRepositorio,
            PersonaRepositorio personaRepositorio,
            EstadoRegistroRepositorio estadoRegistroRepositorio,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // Ejecuta solo si la tabla de roles está vacía (primera vez que corre el proyecto)
            if (rolRepositorio.count() == 0) {
                System.out.println("Inicializando datos de prueba...");

                // 1️. Crear rol ADMIN
                Rol rolAdmin = Rol.builder()
                        .nombre("ADMIN")
                        .descripcion("Administrador con todos los permisos del sistema")
                        .build();
                rolRepositorio.save(rolAdmin);

                // 2️. Crear estados de registro base
                EstadoRegistro estadoActivo = EstadoRegistro.builder().nombre("Activa").build();
                EstadoRegistro estadoInactivo = EstadoRegistro.builder().nombre("Inactiva").build();
                estadoRegistroRepositorio.saveAll(List.of(estadoActivo, estadoInactivo));

                // 3️. Crear persona asociada al usuario administrador
                Persona personaAdmin = Persona.builder()
                        .nombres("Administrador")
                        .apellidos("del Sistema")
                        .documento("00000001")
                        .build();

                // 4️. Crear usuario administrador con contraseña encriptada
                Usuario usuarioAdmin = Usuario.builder()
                        .persona(personaAdmin)
                        .rol(rolAdmin)
                        .correo("admin@certify.com")
                        .contrasena(passwordEncoder.encode("123456"))
                        .build();

                usuarioRepositorio.save(usuarioAdmin);

                // 5️. Mostrar credenciales por consola
                System.out.println("Usuario administrador de prueba creado:");
                System.out.println("Correo: admin@certify.com");
                System.out.println("Clave: 123456");
            }
        };
    }
}
