package com.safeway.financial.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PagamentoDTO(
        UUID id,
        UUID usuarioId,
        LocalDate dataPagamento,
        Double valorPagamento,
        String descricao
) {
}
