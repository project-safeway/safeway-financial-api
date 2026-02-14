package com.safeway.financial.application.usecases.mensalidade;

import com.safeway.financial.application.dto.MensalidadeDTO;

import java.util.UUID;

public interface pagarMensalidadeUseCase {

    MensalidadeDTO registrarPagamento(UUID mensalidadeId, UUID usuarioId);

}
