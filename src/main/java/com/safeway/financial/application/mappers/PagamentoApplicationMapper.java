package com.safeway.financial.application.mappers;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.domain.entities.Pagamento;
import org.springframework.stereotype.Component;

@Component
public class PagamentoApplicationMapper {

    public PagamentoDTO toDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getUsuarioId(),
                pagamento.getDataPagamento(),
                pagamento.getValorPagamento(),
                pagamento.getDescricao()
        );
    }

    public Pagamento toDomain(PagamentoDTO dto) {
        return new Pagamento(
                dto.id(),
                dto.usuarioId(),
                dto.dataPagamento(),
                dto.valorPagamento(),
                dto.descricao()
        );
    }
}

