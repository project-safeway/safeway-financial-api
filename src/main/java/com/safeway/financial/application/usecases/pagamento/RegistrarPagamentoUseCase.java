package com.safeway.financial.application.usecases.pagamento;

import com.safeway.financial.application.dto.PagamentoDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface RegistrarPagamentoUseCase {

    PagamentoDTO registrarNovoPagamento(Input input, UUID usuarioId);

    record Input(
             LocalDate dataPagamento,
             Double valorPagamento,
             String descricao
    ) {
    }

}
