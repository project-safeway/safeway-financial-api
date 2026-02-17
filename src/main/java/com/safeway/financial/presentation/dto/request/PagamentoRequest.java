package com.safeway.financial.presentation.dto.request;

import java.time.LocalDate;

public record PagamentoRequest(
        LocalDate dataPagamento,
        Double valorPagamento,
        String descricao
) {
}
