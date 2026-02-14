package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.usecases.mensalidade.pagarMensalidadeUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.AlunoNotFoundException;
import com.safeway.financial.domain.exceptions.MensalidadeNotFoundException;
import com.safeway.financial.domain.exceptions.MensalidadeWithFinalStatusException;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagarMensalidadeUseCaseImpl implements pagarMensalidadeUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;

    @Override
    public MensalidadeDTO registrarPagamento(UUID mensalidadeId, UUID usuarioId) {
        log.info("Iniciando processo de registrar o pagamento da mensalidade {} do usuário {}", mensalidadeId, usuarioId);

        Mensalidade mensalidade = mensalidadeRepository.buscarPorId(mensalidadeId)
                .orElseThrow(() -> {
                    log.error("Erro ao buscar o mensalidade de id {}", mensalidadeId);
                    return new MensalidadeNotFoundException("Erro ao tentar buscar a mensalidade");
                });

        validarMensalidade(mensalidade);

        AlunoGateway.AlunoData alunoData = alunoGateway.buscarPorId(mensalidade.getAlunoId())
                .orElseThrow(() -> {
                    log.error("Erro ao buscar pelo aluno de id {}", mensalidade.getAlunoId());
                    return new AlunoNotFoundException("Erro ao tentar buscar o aluno");
                });

        if (!alunoData.usuarioId().equals(usuarioId)) {
            log.error("O id do usuário da sessão não corresponde ao id do usuario relacionado ao aluno. ID-SESSAO: {} ID-ALUNO: {}", usuarioId, alunoData.usuarioId());
            throw new OperationNotAlloyedException("Pagamento não permitido");
        }

        mensalidade.marcarComoPago(LocalDate.now());
        mensalidadeRepository.salvar(mensalidade);

        return converterParaDTO(mensalidade, alunoData);
    }

    private void validarMensalidade(Mensalidade mensalidade) {
        if (mensalidade.getDataPagamento() != null || mensalidade.getStatus().equals(StatusPagamento.PAGO)) {
            log.error("Tentativa de registrar pagamento para mensalidade já paga. ID-MENSALIDADE: {}", mensalidade.getId());
            throw new MensalidadeWithFinalStatusException("Mensalidade já se encontra paga");
        }

        if (mensalidade.getStatus().equals(StatusPagamento.CANCELADO)) {
            log.error("Tentativa de registrar pagamento para mensalidade já cancelada. ID-MENSALIDADE: {}", mensalidade.getId());
            throw new MensalidadeWithFinalStatusException("Mensaldiade já cancelada");
        }
    }

    private MensalidadeDTO converterParaDTO(Mensalidade mensalidade, AlunoGateway.AlunoData alunoData) {
        return new MensalidadeDTO(
                mensalidade.getId(),
                mensalidade.getAlunoId(),
                alunoData.nome(),
                mensalidade.getValorMensalidade(),
                mensalidade.getDataVencimento(),
                mensalidade.getStatus(),
                mensalidade.getValorPago(),
                mensalidade.getDataPagamento()
        );
    }
}
