package com.safeway.financial.infrastructure.persistence.repositories.impl;

import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import com.safeway.financial.infrastructure.persistence.entities.MensalidadeEntity;
import com.safeway.financial.infrastructure.persistence.mappers.MensalidadeMapper;
import com.safeway.financial.infrastructure.persistence.repositories.jpa.MensalidadeJpaRepository;
import com.safeway.financial.infrastructure.persistence.specifications.MensalidadeJpaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MensalidadeRepositoryImpl implements MensalidadeRepository {

    private final MensalidadeJpaRepository mensalidadeJpaRepository;
    private final MensalidadeMapper mapper;

    @Override
    public Page<Mensalidade> buscar(MensalidadeSpecification domainSpec, Pageable pageable) {
        Specification<MensalidadeEntity> jpaSpec = MensalidadeJpaSpecification.fromDomain(domainSpec);
        Page<MensalidadeEntity> entityPage = mensalidadeJpaRepository.findAll(jpaSpec, pageable);
        return entityPage.map(mapper::toDomain);
    }

    @Override
    public Optional<Mensalidade> buscarPorId(UUID id) {
        return mensalidadeJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Boolean existeMensalidadesMes(UUID alunoId, LocalDate dataInicio, LocalDate dataFim) {
        return mensalidadeJpaRepository
                .existsByAlunoIdAndDataVencimentoBetween(alunoId, dataInicio, dataFim);
    }

    @Override
    public void salvarTodos(List<Mensalidade> mensalidades) {
        List<MensalidadeEntity> entities = mensalidades.stream()
                .map(mapper::toEntity)
                .toList();
        mensalidadeJpaRepository.saveAll(entities);
    }

    @Override
    public Mensalidade salvar(Mensalidade mensalidade) {
        MensalidadeEntity entity = mapper.toEntity(mensalidade);
        MensalidadeEntity savedEntity = mensalidadeJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deletar(UUID id) {
        mensalidadeJpaRepository.deleteById(id);
    }
}
