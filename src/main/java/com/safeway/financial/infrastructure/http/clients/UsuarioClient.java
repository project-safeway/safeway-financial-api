package com.safeway.financial.infrastructure.http.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "safeway-core",
        contextId = "usuarioClient",
        url = "${safeway.core.url:http://localhost:8080}"
)
public interface UsuarioClient {

    @GetMapping("/usuarios/{id}")
    UsuarioResponse buscarUsuario(@PathVariable UUID id);

    record UsuarioResponse(
            UUID id,
            String nome,
            String email,
            Boolean ativo
    ) {}

}
