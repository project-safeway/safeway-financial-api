package com.safeway.financial.infrastructure.persistence.mappers;

import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.infrastructure.persistence.entities.MensalidadeEntity;
import org.springframework.stereotype.Component;

@Component
public class MensalidadeMapper {

    public Mensalidade toDomain(MensalidadeEntity entity) {
        return new Mensalidade(
                entity.getId(),
                entity.getAlunoId(),
                entity.getDataVencimento(),
                entity.getValorMensalidade(),
                entity.getStatus(),
                entity.getDataPagamento(),
                entity.getValorPago()
        );
    }

    public MensalidadeEntity toEntity(Mensalidade domain) {
        return MensalidadeEntity.builder()
                .id(domain.getId())
                .alunoId(domain.getAlunoId())
                .dataVencimento(domain.getDataVencimento())
                .valorMensalidade(domain.getValorMensalidade())
                .status(domain.getStatus())
                .dataPagamento(domain.getDataPagamento())
                .valorPago(domain.getValorPago())
                .build();
    }

}
