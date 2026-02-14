package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.usecases.mensalidade.pagarMensalidadeUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class pagarMensalidadeUseCaseImpl implements pagarMensalidadeUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;

    @Override
    public void registrarPagamento(UUID id) throws BadRequestException {
        if (id == null) {
            throw new BadRequestException("É necessário o id da mensalidade para registrar o pagamento");
        }

        Mensalidade mensalidade = mensalidadeRepository.buscarPorId(id)
                .orElseThrow(() -> {
                    log.error("Erro ao buscar o mensalidade de id {}", id);
                    return new RuntimeException("Erro ao tentar buscar a mensalidade");
                });

        AlunoGateway.AlunoData alunoData = alunoGateway.buscarPorId(mensalidade.getAlunoId())
                .orElseThrow(() -> {
                    log.error("Erro ao buscar pelo aluno de id {}", mensalidade.getAlunoId());
                    return new RuntimeException("Erro ao tentar buscar o aluno");
                });

        UUID usuarioId = UUID.randomUUID(); // Aqui vai ser substituido pela busca real do id
        if (!alunoData.usuarioId().equals(usuarioId)) {
            log.error("O id do usuário da sessão não corresponde ao id do usuario relacionado ao aluno. ID-SESSAO: {} ID-ALUNO: {}", usuarioId, alunoData.usuarioId());
            throw new BadRequestException("Pagamento não permitido");
        }

        mensalidade.marcarComoPago(LocalDate.now());
        mensalidadeRepository.salvar(mensalidade);
    }
}
