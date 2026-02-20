package com.safeway.financial.infrastructure.http.clients;

import jakarta.websocket.server.PathParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "safeway-core",
        url = "${safeway.core.url:http://localhost:8080}"
)
public interface AlunoClient {

    @GetMapping("/alunos/{id}")
    AlunoResponse buscarAluno(@PathVariable UUID id);

    @GetMapping("/alunos/ativos")
    List<AlunoResponse> buscarTodosAtivos();

    @GetMapping("/alunos/lote")
    List<AlunoResponse> buscarPorIdEmLote(@PathParam("ids") List<UUID> ids);

    record AlunoResponse(
            UUID id,
            String nome,
            Double valorMensalidade,
            Integer diaVencimento,
            Boolean ativo,
            UsuarioResponse usuario
    ) {
        public record UsuarioResponse(UUID idUsuario) {}
    }

}
