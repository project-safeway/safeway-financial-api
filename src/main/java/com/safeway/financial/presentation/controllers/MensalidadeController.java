package com.safeway.financial.presentation.controllers;

import com.safeway.financial.application.ports.input.AuthenticationPort;
import com.safeway.financial.application.usecases.mensalidade.AplicarDescontoUseCase;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadesUseCase;
import com.safeway.financial.application.usecases.mensalidade.CancelarMensalidadeUseCase;
import com.safeway.financial.application.usecases.mensalidade.CriarMensalidadeUseCase;
import com.safeway.financial.application.usecases.mensalidade.PagarMensalidadeUseCase;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.presentation.dto.request.MensalidadeRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private final AuthenticationPort authenticationPort;
    private final BuscarMensalidadesUseCase buscarMensalidadesUseCase;
    private final BuscarMensalidadePorIdUseCase buscarMensalidadePorIdUseCase;
    private final PagarMensalidadeUseCase pagarMensalidadeUseCase;
    private final CancelarMensalidadeUseCase cancelarMensalidadeUseCase;
    private final AplicarDescontoUseCase aplicarDescontoUseCase;
    private final CriarMensalidadeUseCase criarMensalidadeUseCase;
    private final MensalidadeControllerMapper mapper;

    @GetMapping
    public ResponseEntity<Page<MensalidadeResponse>> buscarMensalidades(
            @RequestParam(required = false)UUID alunoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataFim,
            @RequestParam(required = false)List<StatusPagamento> status,
            @PageableDefault(sort = "dataVencimento", direction = Sort.Direction.ASC)Pageable pageable) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        var input = new BuscarMensalidadesUseCase.Input(alunoId, dataInicio, dataFim, status, usuarioId);

        Page<MensalidadeResponse> response = mapper.toResponsePage(buscarMensalidadesUseCase.executar(input, pageable));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MensalidadeResponse> buscarMensalidadePorId(@PathVariable UUID id) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        MensalidadeResponse response = mapper.toResponse(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(id, usuarioId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<Page<MensalidadeResponse>> buscarPendentes(@PageableDefault(sort = "dataVencimento", direction = Sort.Direction.ASC)Pageable pageable) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        var input = new BuscarMensalidadesUseCase.Input(null, null, null, List.of(StatusPagamento.PENDENTE, StatusPagamento.ATRASADO), usuarioId);

        Page<MensalidadeResponse> response = mapper.toResponsePage(buscarMensalidadesUseCase.executar(input, pageable));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/pagar/{id}")
    public ResponseEntity<MensalidadeResponse> registrarPagamento(@PathVariable UUID id) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        MensalidadeResponse response = mapper.toResponse(pagarMensalidadeUseCase.registrarPagamento(id, usuarioId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/cancelar/{id}")
    public ResponseEntity<MensalidadeResponse> cancelarMensalidade(@PathVariable UUID id) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        MensalidadeResponse response = mapper.toResponse(cancelarMensalidadeUseCase.cancelarMensalidade(id, usuarioId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/desconto/{id}")
    public ResponseEntity<MensalidadeResponse> aplicarDesconto(@PathVariable UUID id, @RequestParam Double valorDesconto) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        MensalidadeResponse response = mapper.toResponse(aplicarDescontoUseCase.aplicarDesconto(id, valorDesconto, usuarioId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/criar")
    public ResponseEntity<MensalidadeResponse> criarMensalidade(@RequestBody MensalidadeRequest request) {

        UUID usuarioId = authenticationPort.getCurrentUserId();

        var input = new CriarMensalidadeUseCase.Input(
                request.alunoId(),
                request.dataVencimento(),
                request.valorMensalidade(),
                request.status(),
                request.dataPagamento(),
                request.valorPago()
        );

        MensalidadeResponse response = mapper.toResponse(criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
