package com.certify.backend.servicio;

import com.certify.backend.configuracion.JwtServicio;
import com.certify.backend.dto.PeticionLogin;
import com.certify.backend.dto.RespuestaAutenticacion;
import com.certify.backend.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor // Inyección automática de dependencias final.
public class AutenticacionServicioImpl implements AutenticacionServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final JwtServicio jwtServicio;
    private final AuthenticationManager authenticationManager;

    @Override
    public RespuestaAutenticacion login(PeticionLogin peticion) {
        // Verifica las credenciales (lanza excepción si son inválidas).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        peticion.getCorreo(),
                        peticion.getContrasena()
                )
        );

        // Busca el usuario en la base de datos.
        var usuario = usuarioRepositorio.findByCorreo(peticion.getCorreo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Genera el token JWT para el usuario autenticado.
        var jwtToken = jwtServicio.generarToken(usuario);

        // Devuelve el token en una respuesta.
        return RespuestaAutenticacion.builder()
                .token(jwtToken)
                .build();
    }
}
