package com.safeway.financial.application.usecases.pagamento;

import java.util.UUID;

public interface DeletarPagamentoUseCase {

    void deletarPagamento(UUID pagamentoId, UUID usuarioId);
}
