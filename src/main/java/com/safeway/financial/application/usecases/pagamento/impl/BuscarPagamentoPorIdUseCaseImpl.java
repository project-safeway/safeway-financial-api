package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoPorIdUseCase;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.exceptions.PagamentoNotFoundException;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarPagamentoPorIdUseCaseImpl implements BuscarPagamentoPorIdUseCase {

    private final PagamentoRepository pagamentoRepository;

    @Override
    @Transactional(readOnly = true)
    public PagamentoDTO buscarPagamentoPorId(UUID pagamentoId, UUID usuarioId) {

        Pagamento pagamento = pagamentoRepository.buscarPorId(pagamentoId)
                .orElseThrow(() -> {
                    log.error("Erro ao buscar o pagamento de id {}", pagamentoId);
                    return new PagamentoNotFoundException("Erro ao tentar buscar o pagamento");
                });

        if (!pagamento.getUsuarioId().equals(usuarioId)) {
            log.error("O pagamento de id {} não pertence ao usuário de id {}", pagamentoId, usuarioId);
            throw new PagamentoNotFoundException("Erro ao tentar buscar o pagamento");
        }

        return converterParaDTO(pagamento);
    }

    @Override
    public Pagamento converterParaDomain(PagamentoDTO dto) {
        return new Pagamento(
                dto.id(),
                dto.usuarioId(),
                dto.dataPagamento(),
                dto.valorPagamento(),
                dto.descricao()
        );
    }

    private PagamentoDTO converterParaDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getUsuarioId(),
                pagamento.getDataPagamento(),
                pagamento.getValorPagamento(),
                pagamento.getDescricao()
        );
    }
}
