package com.safeway.financial.presentation.controllers;

import com.safeway.financial.application.usecases.pagamento.RegistrarPagamentoUseCase;
import com.safeway.financial.presentation.dto.request.PagamentoRequest;
import com.safeway.financial.presentation.dto.response.PagamentoResponse;
import com.safeway.financial.presentation.mappers.PagamentoControllerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final RegistrarPagamentoUseCase registrarPagamentoUseCase;
    private final PagamentoControllerMapper mapper;

    @PostMapping("/registrar")
    public ResponseEntity<PagamentoResponse> registrarPagamento(@RequestBody PagamentoRequest request) {

        UUID usuarioId = UUID.randomUUID(); //TODO: Corrigir a buscar do id do usuario

        var input = new RegistrarPagamentoUseCase.Input(
                request.dataPagamento(),
                request.valorPagamento(),
                request.descricao()
        );

        PagamentoResponse response = mapper.toResponse(registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
