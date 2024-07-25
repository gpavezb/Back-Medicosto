package com.generation.medicostos.repository;

import com.generation.medicostos.models.Medicamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    @Query("SELECT m FROM Medicamento m WHERE (m.nombre LIKE %:nombre% OR m.complemento LIKE %:complemento%) AND m.precio > 0 ORDER BY m.precio ASC")
    Page<Medicamento> findMedicamentosByNombreContainingIgnoreCaseOrComplementoContainingIgnoreCaseAndPrecioGreaterThanOrderByPrecioAsc(
            @Param("nombre") String nombre,
            @Param("complemento") String complemento,
            Pageable pageable);
}