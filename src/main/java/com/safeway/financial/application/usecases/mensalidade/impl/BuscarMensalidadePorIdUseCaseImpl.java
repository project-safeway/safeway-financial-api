package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.ports.output.UsuarioGateway;
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
    private final UsuarioGateway usuarioGateway;

    @Override
    @Transactional(readOnly = true)
    public MensalidadeDTO buscarMensalidadePorId(UUID mensalidadeId, UUID usuarioId) {

        if (!usuarioGateway.estaAtivo(usuarioId)) {
            log.error("Usuário com id: {} está inativo. Operação não permitida.", usuarioId);
            throw new OperationNotAlloyedException("Usuário inativo. Não é possível buscar a mensalidade.");
        }

        Mensalidade mensalidade = mensalidadeRepository.buscarPorId(mensalidadeId)
                .orElseThrow(() -> {
                    log.error("Erro ao buscar a mensalidade de id {}", mensalidadeId);
                    return new MensalidadeNotFoundException("Erro ao tentar buscar a mensalidade");
                });

        AlunoGateway.AlunoData alunoData = alunoGateway.buscarPorId(mensalidade.getAlunoId())
                .orElseThrow(() -> {
                    log.error("Erro ao buscar pelo aluno de id {}", mensalidade.getAlunoId());
                    return new AlunoNotFoundException("Erro ao tentar buscar o aluno");
                });

        if (!alunoData.usuarioId().equals(usuarioId)) {
            log.error("O id do usuário da sessão não corresponde ao id do usuario relacionado ao aluno. ID-SESSAO: {} ID-ALUNO: {}",
                    usuarioId, alunoData.usuarioId());
            throw new OperationNotAlloyedException("Operação não permitida");
        }

        return converterParaDTO(mensalidade);
    }

    @Override
    public Mensalidade converterParaDomain(MensalidadeDTO dto) {
        return new Mensalidade(
                dto.id(),
                dto.alunoId(),
                null,
                dto.nomeAluno(),
                dto.dataVencimento(),
                dto.valorMensalidade(),
                dto.status(),
                dto.dataPagamento(),
                dto.valorPago()
        );
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
