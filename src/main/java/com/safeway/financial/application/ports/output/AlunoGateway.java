package com.safeway.financial.application.ports.output;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlunoGateway {

    Optional<AlunoData> buscarPorId(UUID id);
    boolean estaAtivo(UUID id);
    List<AlunoData> buscarTodosAtivos();

    record AlunoData(
            UUID id,
            String nome,
            Double valorMensalidade,
            Integer diaVencimento,
            Boolean ativo,
            UUID usuarioId
    ) {}

}
