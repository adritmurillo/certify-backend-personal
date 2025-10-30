package com.certify.backend.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServicio {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.nombre}")
    private String appNombre;


    public void enviarCredencialesTemporales(String destinatario, String correo, String contrasenaTemporal) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(fromEmail);
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido a " + appNombre + "! - Credenciales de Acceso");
        mensaje.setText(construirMensajeCredenciales(correo, contrasenaTemporal));

        mailSender.send(mensaje);
    }

    private String construirMensajeCredenciales(String correo, String contrasenaTemporal) {
        return String.format("""
            ¡Hola!
            
            Tu solicitud de registro en %s ha sido aprobada.
            
            Tus credenciales de acceso son:
            
            📧 Correo: %s
            🔑 Contraseña: %s
                        
            Puedes acceder al sistema en: https://certify.com/login
            
            Si no solicitaste este acceso, por favor ignora este mensaje.
            
            Saludos,
            Equipo %s
            """, appNombre, correo, contrasenaTemporal, appNombre);
    }
}