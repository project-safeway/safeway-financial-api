package com.safeway.financial.infrastructure.scheduler;

import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MensalidadeScheduler {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void gerarMensalidade() {
        log.info("Iniciando geração de mensalidades do mês");

        LocalDate data = LocalDate.now();
        LocalDate inicioMes = data.withDayOfMonth(1);
        LocalDate fimMes = data.withDayOfMonth(data.lengthOfMonth());

        List<AlunoGateway.AlunoData> alunosAtivos = alunoGateway.buscarTodosAtivos();
        Set<UUID> idsComMensalidade = mensalidadeRepository.buscarIdsAlunosComMensalidadeNoPeriodo(inicioMes, fimMes);

        List<Mensalidade> mensalidadesParaCriar = new ArrayList<>();

        for (AlunoGateway.AlunoData aluno : alunosAtivos) {
            if (!idsComMensalidade.contains(aluno.id())) {
                LocalDate dataVencimento = calcularDataVencimento(data, aluno.diaVencimento());

                Mensalidade mensalidade = new Mensalidade(
                        null,
                        aluno.id(),
                        aluno.nome(),
                        dataVencimento,
                        aluno.valorMensalidade(),
                        StatusPagamento.PENDENTE,
                        null, null);

                mensalidadesParaCriar.add(mensalidade);
            }
        }

        if (!mensalidadesParaCriar.isEmpty()) {
            // WARN: Ele vai funcionar normal, mas em casos que possamos ter mais de uma instacia do scheduler rodando, pode gerar mensalidades duplicadas
            // A melhor forma de evitar isso seria com uma constraint, mas temos que decidir a regra se ele pode criar mais de uma pra mensalidade pra mesma data
            mensalidadeRepository.salvarTodos(mensalidadesParaCriar);
            log.info("Finalizada geração de mensalidades. Total de mensalidades geradas: {}", mensalidadesParaCriar.size());
        } else {
            log.info("Nenhuma mensalidade nova foi gerada, todos os alunos já possuem mensalidade para o período.");
        }
    }

    public LocalDate calcularDataVencimento(LocalDate mesReferencia, Integer diaVencimento) {
        try {
            return mesReferencia.withDayOfMonth(diaVencimento);
        } catch (DateTimeException e) {
            log.warn("Dia de vencimento {} inválido para o mês {}. Ajustando para o último dia do mês.", diaVencimento, mesReferencia.getMonth());
            return mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void atualizarStatusMensalidades() {
        log.info("Iniciando atualizar status mensalidades");
        LocalDate data = LocalDate.now();

        Integer totalAtualizado = mensalidadeRepository.atualizarStatusParaAtrasado(data);
        log.info("Finalizada atualização de status de mensalidades. Total de mensalidades atualizadas: {}", totalAtualizado);
    }

}
