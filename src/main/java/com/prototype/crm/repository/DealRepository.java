package com.prototype.crm.repository;

import com.prototype.crm.model.dto.emuns.DealStage;
import com.prototype.crm.model.entity.Deal;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealRepository extends JpaRepository<Deal, UUID> {

    @Query("""
        SELECT d.stage, COUNT(d), COALESCE(SUM(d.amount), 0)
        FROM Deal d
        GROUP BY d.stage
        ORDER BY CASE d.stage
            WHEN 'PROSPECTION'  THEN 1
            WHEN 'QUALIFICATION' THEN 2
            WHEN 'PROPOSAL'      THEN 3
            WHEN 'NEGOTIATION'   THEN 4
            WHEN 'CLOSED_WON'    THEN 5
        END
    """)
    List<Object[]> getRawPipelineFunnelData();

    long countByStage(DealStage stage);

    // ✅ SOLUCIÓN: Un solo parámetro 'filter' que busca en título O compañía
    @Query("SELECT d FROM Deal d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
            "LOWER(d.company) LIKE LOWER(CONCAT('%', :filter, '%'))")
    List<Deal> searchDealsByFilter(@Param("filter") String filter);

    @Query("SELECT SUM(d.amount) FROM Deal d WHERE d.stage IN :stages")
    Optional<BigDecimal> sumAmountByStageIn(@Param("stages") List<DealStage> stages);

    @Query("""
        SELECT d FROM Deal d 
        WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :term, '%')) 
           OR LOWER(d.company) LIKE LOWER(CONCAT('%', :term, '%'))
    """)
    List<Deal> searchByTitleOrCompany(@Param("term") String term);

}
