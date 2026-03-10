package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.mappers.MensalidadeApplicationMapper;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.application.usecases.mensalidade.PagarMensalidadeUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.MensalidadeWithFinalStatusException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagarMensalidadeUseCaseImpl implements PagarMensalidadeUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final BuscarMensalidadePorIdUseCase buscarMensalidadePorIdUseCase;
    private final MensalidadeApplicationMapper mapper;

    @Override
    public MensalidadeDTO registrarPagamento(UUID mensalidadeId, UUID usuarioId) {
        log.info("Iniciando processo de registrar o pagamento da mensalidade {} do usuário {}", mensalidadeId, usuarioId);

        MensalidadeDTO dto = buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId);
        Mensalidade mensalidade = mapper.toDomain(dto);

        validarMensalidade(mensalidade);

        mensalidade.marcarComoPago(LocalDate.now());
        mensalidadeRepository.salvar(mensalidade);

        return mapper.toDTO(mensalidade);
    }

    private void validarMensalidade(Mensalidade mensalidade) {
        if (mensalidade.getDataPagamento() != null || StatusPagamento.PAGO.equals(mensalidade.getStatus())) {
            log.error("Tentativa de registrar pagamento para mensalidade já paga. ID: {}", mensalidade.getId());
            throw new MensalidadeWithFinalStatusException("Mensalidade já se encontra paga");
        }
        if (StatusPagamento.CANCELADO.equals(mensalidade.getStatus())) {
            log.error("Tentativa de registrar pagamento para mensalidade já cancelada. ID: {}", mensalidade.getId());
            throw new MensalidadeWithFinalStatusException("Mensalidade já cancelada");
        }
    }
}
