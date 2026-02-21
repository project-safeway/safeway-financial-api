package com.safeway.financial.application.usecases.mensalidade;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.domain.entities.Mensalidade;

import java.util.UUID;

public interface BuscarMensalidadePorIdUseCase {

    MensalidadeDTO buscarMensalidadePorId(UUID mensalidadeId, UUID usuarioId);
    Mensalidade converterParaDomain(MensalidadeDTO dto);

}
