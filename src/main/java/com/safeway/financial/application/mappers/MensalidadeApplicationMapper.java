package com.safeway.financial.application.mappers;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.domain.entities.Mensalidade;
import org.springframework.stereotype.Component;

@Component
public class MensalidadeApplicationMapper {

    public MensalidadeDTO toDTO(Mensalidade mensalidade) {
        return new MensalidadeDTO(
                mensalidade.getId(),
                mensalidade.getAlunoId(),
                mensalidade.getValorMensalidade(),
                mensalidade.getDataVencimento(),
                mensalidade.getStatus(),
                mensalidade.getValorPago(),
                mensalidade.getDataPagamento()
        );
    }

    public Mensalidade toDomain(MensalidadeDTO dto) {
        return new Mensalidade(
                dto.id(),
                dto.alunoId(),
                dto.dataVencimento(),
                dto.valorMensalidade(),
                dto.status(),
                dto.dataPagamento(),
                dto.valorPago()
        );
    }
}

