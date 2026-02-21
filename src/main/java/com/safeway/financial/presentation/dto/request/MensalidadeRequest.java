package com.safeway.financial.presentation.dto.request;

import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.UUID;

public record MensalidadeRequest(
        UUID alunoId,
        Double valorMensalidade,
        LocalDate dataVencimento,
        StatusPagamento status,
        Double valorPago,
        LocalDate dataPagamento
) {
}
