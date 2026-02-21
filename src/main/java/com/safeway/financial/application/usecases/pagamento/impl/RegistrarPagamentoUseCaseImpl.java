package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.pagamento.RegistrarPagamentoUseCase;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrarPagamentoUseCaseImpl implements RegistrarPagamentoUseCase {

    private final PagamentoRepository pagamentoRepository;
    private final UsuarioGateway usuarioGateway;

    @Override
    public PagamentoDTO registrarNovoPagamento(Input input, UUID usuarioId) {
        log.info("Registrando novo pagamento: {}", input);

        if (!usuarioGateway.estaAtivo(usuarioId)) {
            log.error("Usuário de id {} não está ativo.", usuarioId);
            throw new OperationNotAlloyedException("Usuário não está ativo.");
        }

        validarEstrutura(input);

        Pagamento pagamento = new Pagamento(
                null,
                usuarioId,
                input.dataPagamento(),
                input.valorPagamento(),
                input.descricao()
        );

        Pagamento pagamentoSalvo = pagamentoRepository.salvar(pagamento);
        return converterParaDTO(pagamentoSalvo);
    }

    private void validarEstrutura(Input input) {
        if (input.dataPagamento() == null) {
            log.error("Data de pagamento é nula.");
            throw new IllegalArgumentException("A data de pagamento é obrigatória.");
        }

        if (input.valorPagamento() == null || input.valorPagamento() <= 0) {
            log.error("Valor de pagamento é inválido: {}", input.valorPagamento());
            throw new IllegalArgumentException("O valor de pagamento deve ser maior que zero.");
        }

        if (input.descricao() == null || input.descricao().isBlank()) {
            log.error("Descrição do pagamento é nula ou vazia.");
            throw new IllegalArgumentException("A descrição do pagamento é obrigatória.");
        }
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
