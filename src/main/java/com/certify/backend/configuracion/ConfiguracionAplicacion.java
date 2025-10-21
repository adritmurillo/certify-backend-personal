package com.certify.backend.configuracion;

import com.certify.backend.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Clase de configuración central para Spring Security.
 * Define los componentes (Beans) necesarios para gestionar la autenticación
 * y la seguridad de la aplicación.
 */

@Configuration
@RequiredArgsConstructor
public class ConfiguracionAplicacion {
    // Dependencias para trabajar con la base de datos de usuarios
    private final UsuarioRepositorio usuarioRepositorio;

    @Bean
    public UserDetailsService userDetailsService(){
        // Esta es la estrategia que usará Spring para buscar un usuario.
        // Cuando necesite un usuario, ejecutará esta función lambda.
        return username -> usuarioRepositorio.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        // Proveedor encargado de la autenticacion
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Aca le indicamos al proveedor como buscara los usuarios
        authProvider.setUserDetailsService(userDetailsService());
        // Y aca le indicamos al proveedor que codificador de contraseñas usar para la verificacion
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // Algoritmo para codificar contraseñas (BCrypt)
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        // Obtiene el gestor de autenticación de Spring, necesario para procesar los inicios de sesión.
        return config.getAuthenticationManager();
    }
}
