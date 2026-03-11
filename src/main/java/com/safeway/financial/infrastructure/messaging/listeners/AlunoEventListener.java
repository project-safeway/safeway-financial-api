package com.safeway.financial.infrastructure.messaging.listeners;

import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import com.safeway.financial.infrastructure.messaging.events.AlunoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlunoEventListener {

    private final MensalidadeRepository mensalidadeRepository;

    @RabbitListener(queues = "${rabbitmq.queues.aluno-criado}")
    @Transactional
    public void onAlunoCriado(@Payload AlunoEvent event) {
        log.info("Recebido evento de aluno criado: {}", event.alunoId());

        try {
            if (!event.ativo()) {
                log.info("Aluno inativo, não gerando mensalidades");
                return;
            }

            LocalDate hoje = LocalDate.now();

            LocalDate mesReferencia = hoje.plusMonths(1);
            LocalDate dataVencimento = calcularDataVencimento(mesReferencia, event.diaVencimento());

            Mensalidade mensalidade = new Mensalidade(
                    null,
                    event.alunoId(),
                    event.nome(),
                    dataVencimento,
                    event.valorMensalidade(),
                    StatusPagamento.PENDENTE,
                    null,
                    null
            );

            mensalidadeRepository.salvar(mensalidade);

            log.info("Mensalidade gerada para aluno {} com vencimento em {}", event.alunoId(), dataVencimento);
        } catch (Exception e) {
            log.error("Erro ao processar evento de aluno criado", e);
            throw e;
        }
    }

    @RabbitListener(queues = "${rabbitmq.queues.aluno-atualizado}")
    @Transactional
    public void onAlunoAtualizado(@Payload AlunoEvent event) {
        log.info("Recebido evento de aluno atualizado: {}", event.alunoId());

        try {

            if (!event.ativo()) {
                log.info("Aluno inativo, não atualizando mensalidades");
                return;
            }

            List<Mensalidade> mensalidades = mensalidadeRepository.buscarPorAlunoId(event.alunoId());

            if (mensalidades.isEmpty()) {
                log.info("Nenhuma mensalidade encontrada para o aluno {}, não atualizando", event.alunoId());
                return;
            }

            List<Mensalidade> mensalidadesPendentes = mensalidades.stream()
                    .filter(m -> m.getStatus().equals(StatusPagamento.PENDENTE))
                    .toList();

            for (Mensalidade mensalidade : mensalidadesPendentes) {
                mensalidade.alterarValorMensalidade(event.valorMensalidade());
                LocalDate novaDataVencimento = calcularDataVencimento(mensalidade.getDataVencimento(), event.diaVencimento());
                mensalidade.alterarDataVencimento(novaDataVencimento);
            }

            mensalidadeRepository.salvarTodos(mensalidadesPendentes);

            log.info("As mensalidades do aluno {} foram atualizadas com sucesso", event.alunoId());
        } catch (Exception e) {
            log.error("Erro ao processar evento de aluno atualizado", e);
            throw e;
        }
    }

    @RabbitListener(queues = "${rabbitmq.queues.aluno-inativado}")
    @Transactional
    public void onAlunoInativado(@Payload AlunoEvent event) {
        log.info("Recebido evento de aluno inativado: {}", event.alunoId());

        try {

            if (event.ativo()) {
                log.info("Aluno ativo, não cancelando mensalidades");
                return;
            }

            List<Mensalidade> mensalidades = mensalidadeRepository.buscarPorAlunoId(event.alunoId());

            if (mensalidades.isEmpty()) {
                log.info("Nenhuma mensalidade encontrada para o aluno {}, não cancelando", event.alunoId());
                return;
            }

            List<Mensalidade> mensalidadesFuturas = mensalidades.stream()
                    .filter(m -> m.getStatus().equals(StatusPagamento.PENDENTE))
                    .toList();

            if (mensalidadesFuturas.isEmpty()) {
                log.info("Nenhuma mensalidade futura encontrada para o aluno {}, não cancelando", event.alunoId());
                return;
            }

            for (Mensalidade mensalidade : mensalidadesFuturas) {
                mensalidade.marcarComoCancelada();
            }

            mensalidadeRepository.salvarTodos(mensalidadesFuturas);

            log.info("As mensalidades do aluno {} foram canceladas com sucesso", event.alunoId());

        } catch (Exception e) {
            log.error("Erro ao processar evento de aluno inativado", e);
            throw e;
        }
    }

    private LocalDate calcularDataVencimento(LocalDate mesReferencia, Integer diaVencimento) {
        try {
            return mesReferencia.withDayOfMonth(diaVencimento);
        } catch (Exception e) {
            return mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
        }
    }

}
