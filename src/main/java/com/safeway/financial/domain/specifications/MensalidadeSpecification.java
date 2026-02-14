package com.safeway.financial.domain.specifications;

import com.safeway.financial.domain.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MensalidadeSpecification {

    private final UUID alunoId;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final List<StatusPagamento> status;
    private final UUID usuarioId;

    public MensalidadeSpecification(Builder builder) {
        this.alunoId = builder.alunoId;
        this.dataInicio = builder.dataInicio;
        this.dataFim = builder.dataFim;
        this.status = builder.status;
        this.usuarioId = builder.usuarioId;
    }

    public UUID getAlunoId() {
        return alunoId;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public List<StatusPagamento> getStatus() {
        return status;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public boolean temFiltros() {
        return alunoId != null ||
                dataInicio != null ||
                dataFim != null ||
                (status != null && !status.isEmpty()) ||
                usuarioId != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID alunoId;
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private List<StatusPagamento> status;
        private UUID usuarioId;

        public Builder alunoId(UUID alunoId) {
            this.alunoId = alunoId;
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

        public Builder status(List<StatusPagamento> status) {
            this.status = status;
            return this;
        }

        public Builder usuarioId(UUID usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public MensalidadeSpecification build() {
            return new MensalidadeSpecification(this);
        }
    }

}
