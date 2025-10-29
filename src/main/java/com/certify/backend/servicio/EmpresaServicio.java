package com.certify.backend.servicio;

import com.certify.backend.dto.PeticionActualizarEmpresa;
import com.certify.backend.dto.PeticionSolicitudEmpresa;
import com.certify.backend.dto.RespuestaEmpresa;

import java.util.List;

public interface EmpresaServicio {

    RespuestaEmpresa solicitarRegistro(PeticionSolicitudEmpresa peticion);

    RespuestaEmpresa aprobarSolicitud(Integer empresaId);

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

    //Cambia el estado de una empresa (ACTIVA, INACTIVA, PENDIENTE)
    RespuestaEmpresa cambiarEstado(Integer empresaId, String nuevoEstado);

    //Elimina una empresa por su ID, soft delete: cambia estado a "Inactivo"
    void eliminarEmpresa(Integer empresaId);
}
