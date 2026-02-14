package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.application.usecases.mensalidade.CancelarMensalidadeUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.MensalidadeWithFinalStatusException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelarMensalidadeUseCaseImpl implements CancelarMensalidadeUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final BuscarMensalidadePorIdUseCase buscarMensalidadePorIdUseCase;

    @Override
    public MensalidadeDTO cancelarMensalidade(UUID mensalidadeId,UUID usuarioId) {
        log.info("Iniciando processo de cancelar mensalidade {} do usuario {}", mensalidadeId, usuarioId);

        MensalidadeDTO dto = buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId);
        Mensalidade mensalidade = converterParaDomain(dto);

        validarMensalidade(mensalidade);

        mensalidade.marcarComoCancelada();
        mensalidadeRepository.salvar(mensalidade);

        return converterParaDTO(mensalidade, dto.nomeAluno());
    }

    private void validarMensalidade(Mensalidade mensalidade) {
        if (mensalidade.getDataPagamento() != null || mensalidade.getStatus().equals(StatusPagamento.PAGO)) {
            log.error("Tentativa de cancelar mensalidade inválida. ID-MENSALIDADE: {}", mensalidade.getId());
            throw new MensalidadeWithFinalStatusException("Mensalidade já se encontra paga");
        }

        if (mensalidade.getStatus().equals(StatusPagamento.CANCELADO)) {
            log.error("Tentativa de cancelar mensalidade inválida. ID-MENSALIDADE: {}", mensalidade.getId());
            throw new MensalidadeWithFinalStatusException("Mensalidade já cancelada");
        }
    }

    private Mensalidade converterParaDomain(MensalidadeDTO dto) {
        return new Mensalidade(
                dto.id(),
                dto.alunoId(),
                dto.dataVencimento(),
                dto.valorMensalidade(),
                dto.status(),
                dto.dataPagamento(),
                dto.valorPago()
        );
    }

    private MensalidadeDTO converterParaDTO(Mensalidade mensalidade, String nomeAluno) {
        return new MensalidadeDTO(
                mensalidade.getId(),
                mensalidade.getAlunoId(),
                nomeAluno,
                mensalidade.getValorMensalidade(),
                mensalidade.getDataVencimento(),
                mensalidade.getStatus(),
                mensalidade.getValorPago(),
                mensalidade.getDataPagamento()
        );
    }
}
