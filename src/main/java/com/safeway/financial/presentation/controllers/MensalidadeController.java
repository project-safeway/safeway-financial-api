package com.safeway.financial.presentation.controllers;

import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadesUseCase;
import com.safeway.financial.application.usecases.mensalidade.pagarMensalidadeUseCase;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.presentation.dto.response.MensalidadeResponse;
import com.safeway.financial.presentation.mappers.MensalidadeControllerMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/mensalidades")
@RequiredArgsConstructor
public class MensalidadeController {

    private final BuscarMensalidadesUseCase buscarMensalidadesUseCase;
    private final pagarMensalidadeUseCase pagarMensalidadeUseCase;
    private final MensalidadeControllerMapper mapper;

    @GetMapping
    public ResponseEntity<Page<MensalidadeResponse>> buscarMensalidades(
            @RequestParam(required = false)UUID alunoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataFim,
            @RequestParam(required = false) List<StatusPagamento> status,
            @PageableDefault(sort = "dataVencimento", direction = Sort.Direction.ASC)Pageable pageable) {

        UUID usuarioId = UUID.randomUUID(); //TODO: Corrigir a buscar do id do usuario

        var input = new BuscarMensalidadesUseCase.Input(alunoId, dataInicio, dataFim, status, usuarioId);

        Page<MensalidadeResponse> response = buscarMensalidadesUseCase
                .executar(input, pageable).map(mapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<Page<MensalidadeResponse>> buscarPendentes(@PageableDefault(sort = "dataVencimento", direction = Sort.Direction.ASC)Pageable pageable) {

        UUID usuarioId = UUID.randomUUID(); //TODO: Corrigir a buscar do id do usuario

        var input = new BuscarMensalidadesUseCase.Input(null, null, null, List.of(StatusPagamento.PENDENTE, StatusPagamento.ATRASADO), usuarioId);

        Page<MensalidadeResponse> response = buscarMensalidadesUseCase
                .executar(input, pageable).map(mapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/pagar/{id}")
    public ResponseEntity<MensalidadeResponse> registrarPagamento(@PathVariable UUID id) {

        UUID usuarioId = UUID.randomUUID(); //TODO: Corrigir a buscar do id do usuario

        MensalidadeResponse response = mapper.toResponse(pagarMensalidadeUseCase.registrarPagamento(id, usuarioId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
