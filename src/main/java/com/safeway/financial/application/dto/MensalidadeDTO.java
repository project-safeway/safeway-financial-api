package com.safeway.financial.application.dto;

import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.UUID;

public record MensalidadeDTO(
        UUID id,
        UUID alunoId,
        Double valorMensalidade,
        LocalDate dataVencimento,
        StatusPagamento status,
        Double valorPago,
        LocalDate dataPagamento
) {

}
