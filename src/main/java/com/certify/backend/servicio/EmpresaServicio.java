package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionCrearEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;

public interface EmpresaServicio {
    // Este metodo nos ayudara a crear una nueva empresa

    RespuestaEmpresa crearEmpresa(PeticionCrearEmpresa peticion);

    // Dentro de esta interfaz podemos poner mas metodos como
    // la busqueda de empresa por id, actualizarla, etc.
}
