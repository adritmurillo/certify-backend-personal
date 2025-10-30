package com.certify.backend.repositorio;

import com.certify.backend.modelo.Empresa;
import com.certify.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepositorio extends JpaRepository<Empresa, Integer> {


    Optional<Empresa> findByRuc(String ruc);

    List<Empresa> findByRazonSocialContainingIgnoreCase(String razonSocial);

    @Query("SELECT e FROM Empresa e WHERE e.estado.nombre = :estadoNombre")
    List<Empresa> findByEstadoNombre(@Param("estadoNombre")String estadoNombre);


    @Query("SELECT e FROM Empresa e WHERE " +
            "e.estado.nombre <> 'Archivado' AND " +
            "(:nombre IS NULL OR e.razonSocial ILIKE CONCAT('%', :nombre, '%')) AND " +
            "(:estado IS NULL OR e.estado.nombre = :estado)")
    List<Empresa> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("estado") String estado
    );


}
