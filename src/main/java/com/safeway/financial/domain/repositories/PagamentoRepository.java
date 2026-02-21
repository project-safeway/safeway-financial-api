package com.safeway.financial.domain.repositories;

import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.specifications.PagamentoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository {

    Page<Pagamento> buscar(PagamentoSpecification domainSpec, Pageable pageable);
    Optional<Pagamento> buscarPorId(UUID id);
    Pagamento salvar(Pagamento pagamento);
    void deletar(UUID id);

}
