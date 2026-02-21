package com.safeway.financial.infrastructure.persistence.repositories.jpa;

import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.infrastructure.persistence.entities.MensalidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MensalidadeJpaRepository extends JpaRepository<MensalidadeEntity, UUID>, JpaSpecificationExecutor<MensalidadeEntity> {

    @Query("SELECT m FROM MensalidadeEntity m WHERE m.alunoId = :alunoId")
    List<MensalidadeEntity> findByAlunoId(@Param("alunoId") UUID alunoId);

    @Query("SELECT DISTINCT m.alunoId FROM MensalidadeEntity m " +
            "WHERE m.dataVencimento BETWEEN :inicio AND :fim")
    Set<UUID> findAlunoIdsComMensalidadeNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Modifying
    @Query("UPDATE MensalidadeEntity m " +
            "SET m.status = :novoStatus " +
            "WHERE m.status = :statusAtual " +
            "AND m.dataVencimento < :dataLimite")
    int updateStatusParaAtrasado(
            @Param("statusAtual") StatusPagamento statusAtual,
            @Param("novoStatus") StatusPagamento novoStatus,
            @Param("dataLimite") LocalDate dataLimite
    );

}
