package com.safeway.financial.presentation.dto.response;

import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.UUID;

public record MensalidadeResponse(
        UUID id,
        UUID alunoId,
        LocalDate dataVencimento,
        Double valorMensalidade,
        StatusPagamento status,
        LocalDate dataPagamento,
        Double valorPago
) {
}
