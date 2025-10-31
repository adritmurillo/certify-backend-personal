package com.certify.backend.repositorio;

import com.certify.backend.modelo.Empresa;
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
    List<Empresa> findByEstadoNombre(@Param("estadoNombre") String estadoNombre);
    @Query(
            value = "SELECT e.* FROM empresa e " + // <-- El cambio clave es "e.*"
                    "JOIN estado_registro er ON e.estado_id = er.estado_id " +
                    "WHERE er.nombre <> 'Archivado' " +
                    "AND (:nombre IS NULL OR CAST(e.razon_social AS TEXT) ILIKE CONCAT('%', :nombre, '%')) " +
                    "AND (:estado IS NULL OR er.nombre = :estado)",
            nativeQuery = true
    )
    List<Empresa> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("estado") String estado
    );
    // --- FIN DE LA MODIFICACIÓN CLAVE ---
}