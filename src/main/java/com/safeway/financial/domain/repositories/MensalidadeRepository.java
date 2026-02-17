package com.safeway.financial.domain.repositories;

import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MensalidadeRepository {

    List<Mensalidade> salvarTodos(List<Mensalidade> mensalidades);
    Mensalidade salvar(Mensalidade mensalidade);
    Page<Mensalidade> buscar(MensalidadeSpecification spec, Pageable pageable);
    Optional<Mensalidade> buscarPorId(UUID id);
    Set<UUID> buscarIdsAlunosComMensalidadeNoPeriodo(LocalDate inicio, LocalDate fim);
    int atualizarStatusParaAtrasado(LocalDate dataLimite);
    void deletar(UUID id);

}
