package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionSolicitudEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;

import java.util.List;

public interface EmpresaServicio {

    RespuestaEmpresa solicitarRegistro(PeticionSolicitudEmpresa peticion);

    RespuestaEmpresa aprobarSolicitud(Integer empresaId);

    List<RespuestaEmpresa> listarTodas();
}
