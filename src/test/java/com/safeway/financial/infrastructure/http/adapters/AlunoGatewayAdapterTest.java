package com.safeway.financial.infrastructure.http.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.infrastructure.http.clients.AlunoClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlunoGatewayAdapterTest {

    @Test
    void rejectsCoreResponseWithoutDriverInsteadOfGeneratingOwnerlessFees() {
        AlunoClient client = mock(AlunoClient.class);
        when(client.buscarTodosAtivos()).thenReturn(List.of(new AlunoClient.AlunoResponse(
                UUID.randomUUID(), "Aluno", 500.0, 15, true, UUID.randomUUID(), null)));

        assertThatThrownBy(() -> new AlunoGatewayAdapter(client).buscarTodosAtivos())
                .rootCause()
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Resposta do core sem transporte ou motorista");
    }

    @Test
    void mapsCoreDriverContractForLookupBatchAndScheduler() throws Exception {
        UUID studentId = UUID.randomUUID();
        UUID transportId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        String json = """
                {"id":"%s","nome":"Aluno","valorMensalidade":500.0,"diaVencimento":15,
                 "ativo":true,"transportId":"%s","motoristaId":"%s"}
                """.formatted(studentId, transportId, driverId);
        AlunoClient.AlunoResponse response = new ObjectMapper().readValue(json, AlunoClient.AlunoResponse.class);
        AlunoClient client = mock(AlunoClient.class);
        when(client.buscarAluno(studentId)).thenReturn(response);
        when(client.buscarTodosAtivos()).thenReturn(List.of(response));
        when(client.buscarPorIdEmLote(List.of(studentId))).thenReturn(List.of(response));
        AlunoGatewayAdapter adapter = new AlunoGatewayAdapter(client);

        AlunoGateway.AlunoData expected = new AlunoGateway.AlunoData(
                studentId, "Aluno", 500.0, 15, true, driverId);
        assertThat(adapter.buscarPorId(studentId)).contains(expected);
        assertThat(adapter.buscarTodosAtivos()).containsExactly(expected);
        assertThat(adapter.buscarPorIdEmLote(List.of(studentId))).containsExactly(expected);
    }
}
