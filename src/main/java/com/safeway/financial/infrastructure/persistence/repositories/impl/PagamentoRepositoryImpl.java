package com.safeway.financial.infrastructure.persistence.repositories.impl;

import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import com.safeway.financial.domain.specifications.PagamentoSpecification;
import com.safeway.financial.infrastructure.persistence.entities.PagamentoEntity;
import com.safeway.financial.infrastructure.persistence.mappers.PagamentoMapper;
import com.safeway.financial.infrastructure.persistence.repositories.jpa.PagamentoJpaRepository;
import com.safeway.financial.infrastructure.persistence.specifications.PagamentoJpaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PagamentoRepositoryImpl implements PagamentoRepository {

    private final PagamentoJpaRepository pagamentoJpaRepository;
    private final PagamentoMapper mapper;

    @Override
    public Page<Pagamento> buscar(PagamentoSpecification domainSpec, Pageable pageable) {
        Specification<PagamentoEntity> jpaSpec = PagamentoJpaSpecification.fromDomain(domainSpec);
        Page<PagamentoEntity> entityPage = pagamentoJpaRepository.findAll(jpaSpec, pageable);
        return entityPage.map(mapper::toDomain);
    }

    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {
        return pagamentoJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        PagamentoEntity entity = mapper.toEntity(pagamento);
        PagamentoEntity savedEntity = pagamentoJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deletar(UUID id) {
        pagamentoJpaRepository.deleteById(id);
    }
}
