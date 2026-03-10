package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.mappers.MensalidadeApplicationMapper;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.exceptions.AlunoNotFoundException;
import com.safeway.financial.domain.exceptions.MensalidadeNotFoundException;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarMensalidadePorIdUseCaseImpl implements BuscarMensalidadePorIdUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;
    private final MensalidadeApplicationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public MensalidadeDTO buscarMensalidadePorId(UUID mensalidadeId, UUID usuarioId) {

        Mensalidade mensalidade = mensalidadeRepository.buscarPorId(mensalidadeId)
                .orElseThrow(() -> {
                    log.error("Mensalidade de id {} não encontrada.", mensalidadeId);
                    return new MensalidadeNotFoundException("Erro ao tentar buscar a mensalidade");
                });

        AlunoGateway.AlunoData alunoData = alunoGateway.buscarPorId(mensalidade.getAlunoId())
                .orElseThrow(() -> {
                    log.error("Aluno de id {} não encontrado.", mensalidade.getAlunoId());
                    return new AlunoNotFoundException("Erro ao tentar buscar o aluno");
                });

        if (!alunoData.usuarioId().equals(usuarioId)) {
            log.error("Usuário {} não tem permissão sobre a mensalidade {}.", usuarioId, mensalidadeId);
            throw new OperationNotAlloyedException("Operação não permitida");
        }

        return mapper.toDTO(mensalidade);
    }
}
