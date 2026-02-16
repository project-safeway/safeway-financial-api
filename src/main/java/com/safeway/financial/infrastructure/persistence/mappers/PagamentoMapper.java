package com.safeway.financial.infrastructure.persistence.mappers;

import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.infrastructure.persistence.entities.PagamentoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    public Pagamento toDomain(PagamentoEntity entity) {
        return new Pagamento(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getDataPagamento(),
                entity.getValorPagamento(),
                entity.getDescricao()
        );
    }

    public PagamentoEntity toEntity(Pagamento domain) {
        return PagamentoEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .dataPagamento(domain.getDataPagamento())
                .valorPagamento(domain.getValorPagamento())
                .descricao(domain.getDescricao())
                .build();
    }

}
