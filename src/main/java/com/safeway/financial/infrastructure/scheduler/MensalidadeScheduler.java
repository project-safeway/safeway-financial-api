package com.safeway.financial.infrastructure.scheduler;

import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

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
        Integer contagemMensalidadeGeradas = 0;

        for (AlunoGateway.AlunoData aluno : alunosAtivos) {
            boolean jaExiste = mensalidadeRepository
                    .existeMensalidadesMes(aluno.id(), inicioMes, fimMes);

            if (!jaExiste) {
                LocalDate dataVencimento = calcularDataVencimento(data, aluno.diaVencimento());

                Mensalidade mensalidade = new Mensalidade(null, aluno.id(), dataVencimento, aluno.valorMensalidade(), StatusPagamento.PENDENTE, null, null);
                mensalidadeRepository.salvar(mensalidade);
                contagemMensalidadeGeradas++;
            }
        }

        log.info("Finalizada geração de mensalidades. Total de mensalidades geradas: {}", contagemMensalidadeGeradas);
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

        MensalidadeSpecification spec = MensalidadeSpecification.builder()
                .dataInicio(data)
                .dataFim(data)
                .status(List.of(StatusPagamento.PENDENTE))
                .build();

        Pageable pageable = Pageable.unpaged();
        Page<Mensalidade> response = mensalidadeRepository.buscar(spec, pageable);

        // TODO: Avaliar uma forma melhor de fazer isso, pois do jeito atual nós carregamos tudo em memória, eu acho
        // Com pouca carga isso é ok, mas com uma carga maior isso pode ser um problema
        response.getContent().forEach(mensalidade -> {
            mensalidade.marcarComoAtrasada();
            log.info("Mensalidade ID {} marcada como ATRASADO", mensalidade.getId());
        });

        mensalidadeRepository.salvarTodos(response.getContent());
        log.info("Finalizada atualização de status de mensalidades. Total de mensalidades atualizadas: {}", response.getContent().size());
    }

}
