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

/**
 * Implementación del servicio de autenticación.
 *
 * Se encarga de validar las credenciales de un usuario y
 * generar un token JWT si la autenticación es exitosa.
 */
@Service
@RequiredArgsConstructor // Inyección automática de dependencias final.
public class AutenticacionServicioImpl implements AutenticacionServicio {

    // Repositorio para consultar usuarios desde la base de datos.
    private final UsuarioRepositorio usuarioRepositorio;

    // Servicio encargado de generar y validar tokens JWT.
    private final JwtServicio jwtServicio;

    // Componente de Spring Security que gestiona la autenticación.
    private final AuthenticationManager authenticationManager;

    /**
     * Autentica al usuario usando sus credenciales (correo y contraseña)
     * y genera un token JWT para autorizar sus futuras peticiones.
     *
     * Flujo:
     *  1. Se valida el correo y la contraseña con el AuthenticationManager.
     *  2. Se busca el usuario en la base de datos.
     *  3. Si existe y las credenciales son correctas, se genera un token JWT.
     *  4. Se devuelve el token en una respuesta.
     *
     * @param peticion datos de inicio de sesión (correo y contraseña)
     * @return objeto con el token JWT generado
     * @throws UsernameNotFoundException si el usuario no existe
     */
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
