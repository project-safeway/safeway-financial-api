package com.safeway.financial.application.usecases.mensalidade;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.domain.enums.StatusPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BuscarMensalidadesUseCase {

    Page<MensalidadeDTO> executar(Input input, Pageable pageable);

    record Input(
            UUID alunoId,
            LocalDate dataInicio,
            LocalDate dataFim,
            List<StatusPagamento> status,
            UUID usuarioId
    ) {}

}
