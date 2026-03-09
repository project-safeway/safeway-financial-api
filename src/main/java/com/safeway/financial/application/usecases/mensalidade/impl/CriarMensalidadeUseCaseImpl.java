package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.CriarMensalidadeUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.AlunoNotFoundException;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriarMensalidadeUseCaseImpl implements CriarMensalidadeUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;
    private final UsuarioGateway usuarioGateway;

    @Override
    public MensalidadeDTO criarNovaMensalidade(Input input, UUID usuarioId) {
        log.info("Criando nova mensalidade para alunoId: {}", input.alunoId());

        if (!usuarioGateway.estaAtivo(usuarioId)) {
            log.error("Usuário com id: {} está inativo. Operação não permitida.", usuarioId);
            throw new OperationNotAlloyedException("Usuário inativo. Não é possível criar mensalidade.");
        }

        AlunoGateway.AlunoData alunoData = alunoGateway.buscarPorId(input.alunoId())
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado para id: {}", input.alunoId());
                    return new AlunoNotFoundException("Aluno não encontrado");
                });

        if (!alunoData.usuarioId().equals(usuarioId)) {
            log.error("Usuário com id: {} não tem permissão para criar mensalidade para alunoId: {}", usuarioId, input.alunoId());
            throw new OperationNotAlloyedException("Usuário não tem permissão para criar mensalidade para este aluno.");
        }

        if (!alunoData.ativo()) {
            log.warn("Aluno com id: {} está inativo. Mensalidade não será criada.", input.alunoId());
            throw new OperationNotAlloyedException("Aluno inativo. Não é possível criar mensalidade.");
        }

        validarEstrutura(input);
        validarCoerencia(input);
        validarRegrasDeNegocio(input);
        log.info("Validações para criação de mensalidade para alunoId: {} concluídas com sucesso.", input.alunoId());

        Mensalidade mensalidade = new Mensalidade(
                null,
                input.alunoId(),
                input.dataVencimento(),
                input.valorMensalidade(),
                input.status() != null ? input.status() : StatusPagamento.PENDENTE,
                input.dataPagamento(),
                input.valorPago()
        );

        Mensalidade mensalidadeSalva = mensalidadeRepository.salvar(mensalidade);
        return converterParaDTO(mensalidadeSalva, alunoData.nome());
    }

    private void validarEstrutura(Input input) {

        if (input.dataVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória.");
        }

        if (input.valorMensalidade() == null || input.valorMensalidade() <= 0) {
            throw new IllegalArgumentException("Valor da mensalidade deve ser maior que zero.");
        }
    }

    private void validarCoerencia(Input input) {
        if (input.valorPago() != null && input.valorPago() < 0) {
            throw new IllegalArgumentException("Valor pago não pode ser negativo.");
        }

        if (input.valorPago() != null &&
                input.valorMensalidade() != null &&
                input.valorPago() > input.valorMensalidade()) {
            throw new IllegalArgumentException("Valor pago não pode ser maior que o valor da mensalidade.");
        }
    }

    private void validarRegrasDeNegocio(Input input) {
        if (input.status() == null) {
            return;
        }
        switch (input.status()) {
            case PAGO -> {
                if (input.dataPagamento() == null || input.valorPago() == null) {
                    throw new IllegalArgumentException("Para status PAGO, dataPagamento e valorPago são obrigatórios.");
                }
            }

            case PENDENTE, ATRASADO -> {
                if (input.dataPagamento() != null || input.valorPago() != null) {
                    throw new IllegalArgumentException("Para PENDENTE ou ATRASADO não deve existir pagamento.");
                }
            }
        }
    }

    private MensalidadeDTO converterParaDTO(Mensalidade mensalidade, String nomeAluno) {
        return new MensalidadeDTO(
                mensalidade.getId(),
                mensalidade.getAlunoId(),
                nomeAluno != null && !nomeAluno.isBlank() ? nomeAluno : "Aluno não encontrado",
                mensalidade.getValorMensalidade(),
                mensalidade.getDataVencimento(),
                mensalidade.getStatus(),
                mensalidade.getValorPago(),
                mensalidade.getDataPagamento()
        );
    }
}
