package com.safeway.financial.presentation.controllers;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.usecases.pagamento.AtualizarPagamentoUseCase;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoPorIdUseCase;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoUseCase;
import com.safeway.financial.application.usecases.pagamento.RegistrarPagamentoUseCase;
import com.safeway.financial.presentation.dto.request.PagamentoRequest;
import com.safeway.financial.presentation.dto.response.PagamentoResponse;
import com.safeway.financial.presentation.mappers.PagamentoControllerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final RegistrarPagamentoUseCase registrarPagamentoUseCase;
    private final BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;
    private final BuscarPagamentoUseCase buscarPagamentoUseCase;
    private final AtualizarPagamentoUseCase atualizarPagamentoUseCase;
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

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> obterPagamentoPorId(@PathVariable UUID id) {
        UUID usuarioId = UUID.randomUUID(); //TODO: Corrigir a buscar do id do usuario

        PagamentoDTO pagamento = buscarPagamentoPorIdUseCase.buscarPagamentoPorId(id, usuarioId);

        PagamentoResponse response = mapper.toResponse(pagamento);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PagamentoResponse>> buscarPagamentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataFim,
            @RequestParam(required = false) Double valorMinimo,
            @RequestParam(required = false) Double valorMaximo,
            @RequestParam(required = false) String descricao,
            @PageableDefault(sort = "dataPagamento", direction = Sort.Direction.ASC) Pageable pageable) {

        UUID usuarioId = UUID.randomUUID(); // TODO: Corrigir a buscar do id do usuario

        var input = new BuscarPagamentoUseCase.Input(usuarioId, dataInicio, dataFim, valorMinimo, valorMaximo, descricao);

        Page<PagamentoResponse> response = buscarPagamentoUseCase.buscarPagamentos(input, pageable).map(mapper::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PagamentoResponse> atualizarPagamento(@PathVariable UUID id, @RequestBody PagamentoRequest request) {

        UUID usuarioId = UUID.randomUUID(); //TODO: Corrigir a buscar do id do usuario

        var input = new AtualizarPagamentoUseCase.Input(
                request.dataPagamento(),
                request.valorPagamento(),
                request.descricao()
        );

        PagamentoResponse response = mapper.toResponse(atualizarPagamentoUseCase.atualizarPagamento(id, input, usuarioId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
