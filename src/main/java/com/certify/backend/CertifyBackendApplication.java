package com.certify.backend;

import com.certify.backend.modelo.Persona;
import com.certify.backend.modelo.Rol;
import com.certify.backend.modelo.Usuario;
import com.certify.backend.repositorio.PersonaRepositorio;
import com.certify.backend.repositorio.RolRepositorio;
import com.certify.backend.repositorio.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CertifyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertifyBackendApplication.class, args);
    }

    // --- EN ESTA PARTE ESTAMOS CREANDO UN SUPERADMIN PARA PROBAR EL DESARROLLO---
    @Bean
    public CommandLineRunner commandLineRunner(
            UsuarioRepositorio usuarioRepositorio,
            RolRepositorio rolRepositorio,
            PersonaRepositorio personaRepositorio,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (rolRepositorio.count() == 0) {
                System.out.println("Creando datos de prueba...");

                Rol rolAdmin = Rol.builder()
                        .nombre("ADMIN")
                        .descripcion("Administrador con todos los permisos")
                        .build();
                rolRepositorio.save(rolAdmin);

                Persona personaAdmin = Persona.builder()
                        .nombres("Administrador")
                        .apellidos("del Sistema")
                        .documento("00000001")
                        .build();

                Usuario usuarioAdmin = Usuario.builder()
                        .persona(personaAdmin) // Le pasamos la persona (nueva)
                        .rol(rolAdmin)         // Le pasamos el rol (ya guardado)
                        .correo("admin@certify.com")
                        .contrasena(passwordEncoder.encode("123456")) // Encriptamos
                        .build();


                usuarioRepositorio.save(usuarioAdmin);

                System.out.println("Usuario administrador de prueba creado:");
                System.out.println("Correo: admin@certify.com");
                System.out.println("Clave: 123456");
            }
        };
    }
}