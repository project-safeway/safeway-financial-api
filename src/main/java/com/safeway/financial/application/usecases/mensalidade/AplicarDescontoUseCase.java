package com.safeway.financial.application.usecases.mensalidade;

import com.safeway.financial.application.dto.MensalidadeDTO;

import java.util.UUID;

public interface AplicarDescontoUseCase {

    MensalidadeDTO aplicarDesconto(UUID mensalidadeId, Double valorDesconto, UUID usuarioId);

}
