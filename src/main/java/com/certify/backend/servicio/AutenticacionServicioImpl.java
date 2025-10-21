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
@RequiredArgsConstructor
public class AutenticacionServicioImpl implements AutenticacionServicio {
    // Herramientas que necesitamos

    private final UsuarioRepositorio usuarioRepositorio;
    private final JwtServicio jwtServicio;
    private final AuthenticationManager authenticationManager;

    @Override
    public RespuestaAutenticacion login(PeticionLogin peticion) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(peticion.getCorreo(), peticion.getContrasena()));
        var usuario = usuarioRepositorio.findByCorreo(peticion.getCorreo()).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        var jwtToken = jwtServicio.generarToken(usuario);
        return RespuestaAutenticacion.builder().token(jwtToken).build();
    }
}
