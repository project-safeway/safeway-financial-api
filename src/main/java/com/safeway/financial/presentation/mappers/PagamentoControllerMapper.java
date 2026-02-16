package com.safeway.financial.presentation.mappers;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.presentation.dto.response.PagamentoResponse;
import org.springframework.stereotype.Component;

@Component
public class PagamentoControllerMapper {

    public PagamentoResponse toResponse(PagamentoDTO dto) {
        return new PagamentoResponse(
                dto.id(),
                dto.dataPagamento(),
                dto.valorPagamento(),
                dto.descricao()
        );
    }

}
