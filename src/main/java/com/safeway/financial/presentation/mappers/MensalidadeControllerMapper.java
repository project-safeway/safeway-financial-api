package com.safeway.financial.presentation.mappers;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.presentation.dto.response.MensalidadeResponse;
import org.springframework.stereotype.Component;

@Component
public class MensalidadeControllerMapper {

    public MensalidadeResponse toResponse(MensalidadeDTO dto) {
        return new MensalidadeResponse(
                dto.id(),
                dto.alunoId(),
                dto.nomeAluno(),
                dto.dataVencimento(),
                dto.valorMensalidade(),
                dto.status(),
                dto.dataPagamento(),
                dto.valorPago()
        );
    }

}
