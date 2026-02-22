package com.safeway.financial.infrastructure.http.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "safeway-core",
        contextId = "alunoClient",
        url = "${safeway.core.url:http://localhost:8080}"
)
public interface AlunoClient {

    @GetMapping("/alunos/{id}")
    AlunoResponse buscarAluno(@PathVariable UUID id);

    @GetMapping("/alunos/ativos")
    List<AlunoResponse> buscarTodosAtivos();

    @PostMapping("/alunos/lote")
    List<AlunoResponse> buscarPorIdEmLote(@RequestBody List<UUID> ids);

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
