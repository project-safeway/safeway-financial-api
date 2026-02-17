package com.safeway.financial.application.usecases.mensalidade;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.UUID;

public interface CriarMensalidadeUseCase {

    MensalidadeDTO criarNovaMensalidade(Input input, UUID usuarioId);

    record Input(
            UUID alunoId,
            LocalDate dataVencimento,
            Double valorMensalidade,
            StatusPagamento status,
            LocalDate dataPagamento,
            Double valorPago
    ) {
    }

}
