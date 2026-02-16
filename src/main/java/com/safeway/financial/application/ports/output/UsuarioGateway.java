package com.safeway.financial.application.ports.output;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {

    Optional<UsuarioData> buscarPorId(UUID id);
    boolean estaAtivo(UUID id);

    record UsuarioData(
            UUID id,
            String nome,
            String email,
            Boolean ativo
    ) {}
}
