package com.safeway.financial.infrastructure.persistence.repositories.jpa;

import com.safeway.financial.infrastructure.persistence.entities.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID>, JpaSpecificationExecutor<PagamentoEntity> {
}
