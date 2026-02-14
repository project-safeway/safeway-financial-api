package com.safeway.financial.domain.repositories;

import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MensalidadeRepository {

    Page<Mensalidade> buscar(MensalidadeSpecification spec, Pageable pageable);
    Optional<Mensalidade> buscarPorId(UUID id);
    Mensalidade salvar(Mensalidade mensalidade);
    void deletar(UUID id);

}
