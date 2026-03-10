package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.mappers.PagamentoApplicationMapper;
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
    private final PagamentoApplicationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PagamentoDTO buscarPagamentoPorId(UUID pagamentoId, UUID usuarioId) {

        Pagamento pagamento = pagamentoRepository.buscarPorId(pagamentoId)
                .orElseThrow(() -> {
                    log.error("Pagamento de id {} não encontrado.", pagamentoId);
                    return new PagamentoNotFoundException("Erro ao tentar buscar o pagamento");
                });

        if (!pagamento.getUsuarioId().equals(usuarioId)) {
            log.error("Pagamento {} não pertence ao usuário {}.", pagamentoId, usuarioId);
            throw new PagamentoNotFoundException("Erro ao tentar buscar o pagamento");
        }

        return mapper.toDTO(pagamento);
    }
}
