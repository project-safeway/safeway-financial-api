package com.safeway.financial.infrastructure.persistence.repositories.jpa;

import com.safeway.financial.infrastructure.persistence.entities.MensalidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MensalidadeJpaRepository extends JpaRepository<MensalidadeEntity, UUID>, JpaSpecificationExecutor<MensalidadeEntity> {

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM MensalidadeEntity m " +
           "WHERE m.alunoId = :alunoId " +
           "AND m.dataVencimento BETWEEN :dataInicio AND :dataFim")
    Boolean existsByAlunoIdAndDataVencimentoBetween(
            @Param("alunoId") UUID alunoId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

}
