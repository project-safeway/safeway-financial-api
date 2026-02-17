package com.safeway.financial.application.usecases.pagamento;

import com.safeway.financial.application.dto.PagamentoDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface AtualizarPagamentoUseCase {

    PagamentoDTO atualizarPagamento(UUID pagamentoId, Input input, UUID usuarioId);

    record Input(
            LocalDate dataPagamento,
            Double valorPagamento,
            String descricao
    ) {}

}
