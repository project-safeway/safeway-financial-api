package com.safeway.financial.infrastructure.persistence.repositories.jpa;

import com.safeway.financial.infrastructure.persistence.entities.MensalidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MensalidadeJpaRepository extends JpaRepository<MensalidadeEntity, UUID>, JpaSpecificationExecutor<MensalidadeEntity> {
}
