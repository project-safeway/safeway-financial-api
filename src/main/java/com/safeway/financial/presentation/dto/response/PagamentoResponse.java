package com.safeway.financial.presentation.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record PagamentoResponse(
        UUID id,
        LocalDate dataPagamento,
        Double valorPagamento,
        String descricao
) {
}
