package com.safeway.financial.domain.entities;

import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.UUID;

public class Mensalidade {

    private UUID id;
    private UUID alunoId;
    private LocalDate dataVencimento;
    private Double valorMensalidade;
    private StatusPagamento status;
    private LocalDate dataPagamento;
    private Double valorPago;

    public Mensalidade() {

    }

    public Mensalidade(UUID id, UUID alunoId, LocalDate dataVencimento, Double valorMensalidade, StatusPagamento status, LocalDate dataPagamento, Double valorPago) {
        this.id = id;
        this.alunoId = alunoId;
        this.dataVencimento = dataVencimento;
        this.valorMensalidade = valorMensalidade;
        this.status = status;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAlunoId() {
        return alunoId;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public Double getValorMensalidade() {
        return valorMensalidade;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public Double getValorPago() {
        return valorPago;
    }

    public void marcarComoPago(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
        this.valorPago = valorMensalidade;
        this.status = StatusPagamento.PAGO;
    }

    public boolean estaAtrasada() {
        return LocalDate.now().isAfter(this.dataVencimento) &&
                StatusPagamento.PENDENTE.equals(this.status);
    }
}
