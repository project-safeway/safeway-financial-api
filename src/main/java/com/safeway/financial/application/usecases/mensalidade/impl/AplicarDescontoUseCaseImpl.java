package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.AplicarDescontoUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.exceptions.MensalidadeNotFoundException;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.exceptions.ValorDescontoNotValidException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AplicarDescontoUseCaseImpl implements AplicarDescontoUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final UsuarioGateway usuarioGateway;

    @Override
    public MensalidadeDTO aplicarDesconto(UUID mensalidadeId, Double valorDesconto, UUID usuarioId) {

        if (!usuarioGateway.estaAtivo(usuarioId)) {
            log.error("Usuário com id: {} está inativo. Operação não permitida.", usuarioId);
            throw new OperationNotAlloyedException("Usuário inativo. Não é possível aplicar o desconto a mensalidade.");
        }

        if (valorDesconto == null || valorDesconto <= 0) {
            throw new ValorDescontoNotValidException("Valor de desconto deve ser maior que zero.");
        }

        Mensalidade mensalidade = mensalidadeRepository.buscarPorIdEUsuarioId(mensalidadeId, usuarioId)
            .orElseThrow(() -> new MensalidadeNotFoundException("Erro ao tentar buscar a mensalidade"));

        if (valorDesconto >= mensalidade.getValorMensalidade()) {
            throw new ValorDescontoNotValidException("Valor de desconto não pode ser maior ou igual ao valor da mensalidade.");
        }

        mensalidade.aplicarDesconto(valorDesconto);
        mensalidadeRepository.salvar(mensalidade);
        return converterParaDTO(mensalidade);
    }

    private MensalidadeDTO converterParaDTO(Mensalidade mensalidade) {
        return new MensalidadeDTO(
                mensalidade.getId(),
                mensalidade.getAlunoId(),
                mensalidade.getNomeAluno(),
                mensalidade.getValorMensalidade(),
                mensalidade.getDataVencimento(),
                mensalidade.getStatus(),
                mensalidade.getValorPago(),
                mensalidade.getDataPagamento()
        );
    }
}
