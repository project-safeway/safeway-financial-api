package com.safeway.financial.infrastructure.http.adapters;

import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.infrastructure.http.clients.AlunoClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlunoGatewayAdapter implements AlunoGateway {

    private final AlunoClient alunoClient;

    @Override
    public Optional<AlunoData> buscarPorId(UUID id) {
        try {
            log.debug("Buscando dados do aluno {} no serviço principal", id);

            AlunoClient.AlunoResponse response = alunoClient.buscarAluno(id);

            return Optional.of(converterParaData(response));
        } catch (FeignException.NotFound ex) {
            log.warn("Aluno {} não encontrado no serviço principal", id);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Erro ao buscar aluno {} no serviço principal", id, ex);
            throw new RuntimeException("Erro ao comunicar com o serviço principal", ex);
        }
    }

    @Override
    public boolean estaAtivo(UUID id) {
        return buscarPorId(id).map(AlunoData::ativo).orElse(false);
    }

    @Override
    public List<AlunoData> buscarTodosAtivos() {
        try {
            log.debug("Buscando todos os alunos ativos no serviço principal");

            List<AlunoClient.AlunoResponse> response = alunoClient.buscarTodosAtivos();

            return response.stream()
                    .map(this::converterParaData)
                    .toList();
        } catch (FeignException.NotFound ex) {
            log.warn("Nenhum aluno ativo encontrado no serviço principal");
            return new ArrayList<>();
        } catch (Exception ex) {
            log.error("Erro ao buscar alunos no serviço principal", ex);
            throw new RuntimeException("Erro ao comunicar com o serviço principal", ex);
        }
    }

    @Override
    public List<AlunoData> buscarPorIdEmLote(List<UUID> ids) {
        try {
            log.debug("Buscando dados dos alunos em lote - IDs: {}", ids);

            List<AlunoClient.AlunoResponse> response = alunoClient.buscarPorIdEmLote(ids);

            return response.stream()
                    .map(this::converterParaData)
                    .toList();
        } catch (FeignException.NotFound ex) {
            log.warn("Nenhum aluno encontrado para os IDs fornecidos no serviço principal");
            return new ArrayList<>();
        } catch (Exception ex) {
            log.error("Erro ao buscar alunos em lote no serviço principal", ex);
            throw new RuntimeException("Erro ao comunicar com o serviço principal", ex);
        }

    }

    private AlunoData converterParaData(AlunoClient.AlunoResponse response) {
        if (response.transportId() == null || response.motoristaId() == null) {
            throw new IllegalStateException("Resposta do core sem transporte ou motorista para o aluno " + response.id());
        }
        return new AlunoData(
                response.id(),
                response.nome(),
                response.valorMensalidade(),
                response.diaVencimento(),
                response.ativo(),
                response.motoristaId()
        );
    }
}
