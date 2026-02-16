package com.safeway.financial.domain.specifications;

import java.time.LocalDate;
import java.util.UUID;

public class PagamentoSpecification {

    private final UUID usuarioId;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final Double valorMinimo;
    private final Double valorMaximo;
    private final String descricao;

    public PagamentoSpecification(Builder builder) {
        this.usuarioId = builder.usuarioId;
        this.dataInicio = builder.dataInicio;
        this.dataFim = builder.dataFim;
        this.valorMinimo = builder.valorMinimo;
        this.valorMaximo = builder.valorMaximo;
        this.descricao = builder.descricao;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public Double getValorMinimo() {
        return valorMinimo;
    }

    public Double getValorMaximo() {
        return valorMaximo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean temFiltros() {
        return usuarioId != null ||
                dataInicio != null ||
                dataFim != null ||
                valorMinimo != null ||
                valorMaximo != null ||
                (descricao != null && !descricao.isEmpty());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID usuarioId;
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private Double valorMinimo;
        private Double valorMaximo;
        private String descricao;

        public Builder usuarioId(UUID usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public Builder dataInicio(LocalDate dataInicio) {
            this.dataInicio = dataInicio;
            return this;
        }

        public Builder dataFim(LocalDate dataFim) {
            this.dataFim = dataFim;
            return this;
        }

        public Builder valorMinimo(Double valorMinimo) {
            this.valorMinimo = valorMinimo;
            return this;
        }

        public Builder valorMaximo(Double valorMaximo) {
            this.valorMaximo = valorMaximo;
            return this;
        }

        public Builder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public PagamentoSpecification build() {
            return new PagamentoSpecification(this);
        }
    }
}
