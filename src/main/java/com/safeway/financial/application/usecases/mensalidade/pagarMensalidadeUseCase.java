package com.safeway.financial.application.usecases.mensalidade;

import org.apache.coyote.BadRequestException;

import java.util.UUID;

public interface pagarMensalidadeUseCase {

    void registrarPagamento(UUID id) throws BadRequestException;

}
