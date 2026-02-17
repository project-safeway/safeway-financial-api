package com.safeway.financial.application.usecases.pagamento;

import com.safeway.financial.application.dto.PagamentoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface BuscarPagamentoUseCase {

    Page<PagamentoDTO> buscarPagamentos(Input input, Pageable pageable);

    record Input(
            UUID usuarioId,
            LocalDate dataInicio,
            LocalDate dataFim,
            Double valorMinimo,
            Double valorMaximo,
            String descricao
    ) {}

}
