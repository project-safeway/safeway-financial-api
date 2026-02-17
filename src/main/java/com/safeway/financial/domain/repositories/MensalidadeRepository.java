package com.safeway.financial.domain.repositories;

import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MensalidadeRepository {

    Page<Mensalidade> buscar(MensalidadeSpecification spec, Pageable pageable);
    Optional<Mensalidade> buscarPorId(UUID id);
    Boolean existeMensalidadesMes(UUID alunoId, LocalDate dataInicio, LocalDate dataFim);
    void salvarTodos(List<Mensalidade> mensalidades);
    Mensalidade salvar(Mensalidade mensalidade);
    void deletar(UUID id);

}
