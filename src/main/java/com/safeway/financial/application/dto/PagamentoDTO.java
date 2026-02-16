package com.safeway.financial.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PagamentoDTO(
        UUID id,
        LocalDate dataPagamento,
        Double valorPagamento,
        String descricao
) {
}
