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

    @GetMapping("/students/feign/{id}")
    AlunoResponse buscarAluno(@PathVariable UUID id);

    @GetMapping("/students/ativos")
    List<AlunoResponse> buscarTodosAtivos();

    @PostMapping("/students/lote")
    List<AlunoResponse> buscarPorIdEmLote(@RequestBody List<UUID> ids);

    record AlunoResponse(
            UUID id,
            String nome,
            Double valorMensalidade,
            Integer diaVencimento,
            Boolean ativo,
            UUID transportId,
            UUID motoristaId
    ) {}

}
