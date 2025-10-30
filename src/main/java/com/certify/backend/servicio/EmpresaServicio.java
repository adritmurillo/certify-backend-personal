package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionActualizarEmpresa;
import com.certify.backend.dto.PeticionSolicitudEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;

import java.util.List;

public interface EmpresaServicio {

    RespuestaEmpresa solicitarRegistro(PeticionSolicitudEmpresa peticion);

    RespuestaEmpresa aprobarSolicitud(Integer empresaId);

    RespuestaEmpresa rechazarSolicitud(Integer empresaId, String motivo);

    List<RespuestaEmpresa> listarTodas();

    // PARA UN CRUD MAS COMPLETO AGREGARE ESTOS NUEVOS METODOS


    // Este metodo obtiene una empresa por su ID, si es SUPERADMIN puede ver cualquier empresa
    // si es ADMIN solo puede ver su propia empresa
    RespuestaEmpresa obtenerPorId(Integer empresaId);

    // En este metodo el superadmin puede ver todas las empresas con filtros opcionales
    // el admin solo puede ver su propia empresa
    List<RespuestaEmpresa> listarConFiltros(String nombre, String estado);

    //EN este metodo se actualizan los datos modificables de una empresa, excepto
    // el RUC que es inmutable, el admin solo puede actualizar su propia empresa
    RespuestaEmpresa actualizarEmpresa(Integer empresaId, PeticionActualizarEmpresa peticion);

    //Cambia el estado de una empresa (ACTIVA, RECHAZADA, PENDIENTE)
    RespuestaEmpresa cambiarEstado(Integer empresaId, String nuevoEstado);

    //Este es un softdelete, en realidad se archiva la empresa
    void eliminarEmpresa(Integer empresaId);

    void actualizarCorreoAdmin(Integer empresaId, String nuevoCorreo);

    void eliminarSolicitudRechazada(Integer empresaId);
}
