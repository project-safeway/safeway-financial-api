package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.mappers.PagamentoApplicationMapper;
import com.safeway.financial.application.usecases.pagamento.AtualizarPagamentoUseCase;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoPorIdUseCase;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizarPagamentoUseCaseImpl implements AtualizarPagamentoUseCase {

    private final PagamentoRepository pagamentoRepository;
    private final BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;
    private final PagamentoApplicationMapper mapper;

    @Override
    public PagamentoDTO atualizarPagamento(UUID pagamentoId, Input input, UUID usuarioId) {
        log.info("Iniciando atualizar pagamento: {}", input);

        PagamentoDTO dto = buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId);
        Pagamento pagamento = mapper.toDomain(dto);

        if (input.dataPagamento() != null) pagamento.setDataPagamento(input.dataPagamento());
        if (input.valorPagamento() != null) pagamento.setValorPagamento(input.valorPagamento());
        if (input.descricao() != null) pagamento.setDescricao(input.descricao());

        return mapper.toDTO(pagamentoRepository.salvar(pagamento));
    }
}
