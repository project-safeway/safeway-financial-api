package com.safeway.financial.domain.entities;

import java.time.LocalDate;
import java.util.UUID;

public class Pagamento {

    private UUID id;
    private UUID usuarioId;
    private LocalDate dataPagamento;
    private Double valorPagamento;
    private String descricao;

    public Pagamento() {
    }

    public Pagamento(UUID id, UUID usuarioId, LocalDate dataPagamento, Double valorPagamento, String descricao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.dataPagamento = dataPagamento;
        this.valorPagamento = valorPagamento;
        this.descricao = descricao;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public Double getValorPagamento() {
        return valorPagamento;
    }

    public String getDescricao() {
        return descricao;
    }
}
