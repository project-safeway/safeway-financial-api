package com.safeway.financial.application.usecases.pagamento;

import com.safeway.financial.application.dto.PagamentoDTO;

import java.util.UUID;

public interface BuscarPagamentoPorIdUseCase {

    PagamentoDTO buscarPagamentoPorId(UUID pagamentoId, UUID usuarioId);

}
