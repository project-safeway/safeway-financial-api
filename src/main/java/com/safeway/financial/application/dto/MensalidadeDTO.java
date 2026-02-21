package com.safeway.financial.application.dto;

import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.UUID;

public record MensalidadeDTO(
        UUID id,
        UUID alunoId,
        String nomeAluno,
        Double valorMensalidade,
        LocalDate dataVencimento,
        StatusPagamento status,
        Double valorPago,
        LocalDate dataPagamento
) {

    public boolean estaPendente() {
        return StatusPagamento.PENDENTE.equals(status);
    }

    public boolean estaAtrasado() {
        return StatusPagamento.ATRASADO.equals(status);
    }

    public boolean estaPaga() {
        return StatusPagamento.PAGO.equals(status);
    }

}
