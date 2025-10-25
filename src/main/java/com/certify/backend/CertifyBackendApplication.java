package com.certify.backend;

import com.certify.backend.modelo.EstadoRegistro;
import com.certify.backend.modelo.Persona;
import com.certify.backend.modelo.Rol;
import com.certify.backend.modelo.TipoDocumento;
import com.certify.backend.modelo.Usuario;
import com.certify.backend.repositorio.EstadoRegistroRepositorio;
import com.certify.backend.repositorio.PersonaRepositorio;
import com.certify.backend.repositorio.RolRepositorio;
import com.certify.backend.repositorio.TipoDocumentoRepositorio;
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
            TipoDocumentoRepositorio tipoDocumentoRepositorio,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
        	System.out.println("10:");
                System.out.println("1:");
                System.out.println("Inicializando datos de prueba...");

                Rol rolAdmin = rolRepositorio.findById(1).get();
                TipoDocumento tipoDocumentoDni = tipoDocumentoRepositorio.findById(1).get();


                try {
                	
                	Persona personaAdmin = Persona.builder()
                            .tipoDocumento(tipoDocumentoDni)
                            .nombres("Administrador")
                            .apellidos("del Sistema")
                            .documento("00000001")
                            .build();
                    personaRepositorio.save(personaAdmin);

                    Usuario usuarioAdmin = Usuario.builder()
                            .persona(personaAdmin)
                            .rol(rolAdmin)
                            .correo("admin@certify.com")
                            .contrasena(passwordEncoder.encode("123456"))
                            .build();
                    usuarioRepositorio.save(usuarioAdmin);
				} catch (Exception e) {
					System.out.println(e);
				}

                System.out.println("✅ Usuario administrador de prueba creado:");
                System.out.println("Correo: admin@certify.com");
                System.out.println("Clave: 123456");
            
        };
    }

}
