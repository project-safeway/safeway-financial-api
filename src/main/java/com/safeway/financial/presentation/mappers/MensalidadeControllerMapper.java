package com.safeway.financial.presentation.mappers;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.presentation.dto.response.MensalidadeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MensalidadeControllerMapper {

    private final AlunoGateway alunoGateway;

    public MensalidadeResponse toResponse(MensalidadeDTO dto) {
        String nomeAluno = alunoGateway.buscarPorId(dto.alunoId())
                .map(AlunoGateway.AlunoData::nome)
                .orElse("Aluno não encontrado");

        return buildResponse(dto, nomeAluno);
    }

    public Page<MensalidadeResponse> toResponsePage(Page<MensalidadeDTO> page) {
        List<UUID> alunoIds = page.getContent().stream()
                .map(MensalidadeDTO::alunoId)
                .distinct()
                .toList();

        Map<UUID, String> nomesCache = alunoGateway.buscarPorIdEmLote(alunoIds).stream()
                .collect(Collectors.toMap(AlunoGateway.AlunoData::id, AlunoGateway.AlunoData::nome));

        return page.map(dto -> buildResponse(dto, nomesCache.getOrDefault(dto.alunoId(), "Aluno não encontrado")));
    }

    private MensalidadeResponse buildResponse(MensalidadeDTO dto, String nomeAluno) {
        return new MensalidadeResponse(
                dto.id(),
                dto.alunoId(),
                nomeAluno,
                dto.dataVencimento(),
                dto.valorMensalidade(),
                dto.status(),
                dto.dataPagamento(),
                dto.valorPago()
        );
    }
}
