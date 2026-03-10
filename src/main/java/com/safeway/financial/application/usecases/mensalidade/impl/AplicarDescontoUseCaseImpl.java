package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.mappers.MensalidadeApplicationMapper;
import com.safeway.financial.application.usecases.mensalidade.AplicarDescontoUseCase;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
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
    private final BuscarMensalidadePorIdUseCase buscarMensalidadePorIdUseCase;
    private final MensalidadeApplicationMapper mapper;

    @Override
    public MensalidadeDTO aplicarDesconto(UUID mensalidadeId, Double valorDesconto, UUID usuarioId) {
        if (valorDesconto == null || valorDesconto <= 0) {
            throw new ValorDescontoNotValidException("Valor de desconto deve ser maior que zero.");
        }

        MensalidadeDTO dto = buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId);
        Mensalidade mensalidade = mapper.toDomain(dto);

        if (valorDesconto >= mensalidade.getValorMensalidade()) {
            throw new ValorDescontoNotValidException("Valor de desconto não pode ser maior ou igual ao valor da mensalidade.");
        }

        mensalidade.aplicarDesconto(valorDesconto);
        mensalidadeRepository.salvar(mensalidade);

        return mapper.toDTO(mensalidade);
    }
}
