package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoPorIdUseCase;
import com.safeway.financial.application.usecases.pagamento.DeletarPagamentoUseCase;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletarPagamentoUseCaseImpl implements DeletarPagamentoUseCase {

    private final BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;
    private final PagamentoRepository pagamentoRepository;

    @Override
    public void deletarPagamento(UUID pagamentoId, UUID usuarioId) {
        log.info("Deletando pagamento {}", pagamentoId);

        PagamentoDTO pagamento = buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId);
        pagamentoRepository.deletar(pagamentoId);
    }
}
