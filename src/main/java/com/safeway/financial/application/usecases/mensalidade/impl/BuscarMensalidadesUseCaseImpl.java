package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadesUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarMensalidadesUseCaseImpl implements BuscarMensalidadesUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;
    private final UsuarioGateway usuarioGateway;

    @Override
    @Transactional(readOnly = true)
    public Page<MensalidadeDTO> executar(Input input, Pageable pageable) {
        if (!usuarioGateway.estaAtivo(input.usuarioId())) {
            log.error("Usuário com id: {} está inativo. Operação não permitida.", input.usuarioId());
            throw new IllegalStateException("Usuário inativo. Não é possível buscar as mensalidades.");
        }

        log.info("Buscando mensalidades - Filtros: {}, Página: {}, Tamanho: {}",
                input, pageable.getPageNumber(), pageable.getPageSize());

        MensalidadeSpecification spec = MensalidadeSpecification.builder()
                .alunoId(input.alunoId())
                .dataInicio(input.dataInicio())
                .dataFim(input.dataFim())
                .status(input.status())
                .usuarioId(input.usuarioId())
                .build();

        Page<Mensalidade> mensalidadesPage = mensalidadeRepository.buscar(spec, pageable);

        log.info("Encontradas {} mensalidades (total: {})",
                mensalidadesPage.getNumberOfElements(),
                mensalidadesPage.getTotalElements());

        Map<UUID, AlunoGateway.AlunoData> alunosCache = buscarAlunosEmLote(mensalidadesPage);

        return mensalidadesPage.map(mensalidade -> converterParaDTO(mensalidade, alunosCache));
    }

    private Map<UUID, AlunoGateway.AlunoData> buscarAlunosEmLote(Page<Mensalidade> mensalidadesPage) {
        Map<UUID, AlunoGateway.AlunoData> cache = new HashMap<>();

        List<UUID> alunoIds = mensalidadesPage.getContent().stream()
                .map(Mensalidade::getAlunoId)
                .distinct()
                .toList();

        alunoGateway.buscarPorIdEmLote(alunoIds).forEach(alunoData -> cache.put(alunoData.id(), alunoData));

        return cache;
    }

    private MensalidadeDTO converterParaDTO(Mensalidade mensalidade,
                                            Map<UUID, AlunoGateway.AlunoData> alunosCache) {
        AlunoGateway.AlunoData aluno = alunosCache.get(mensalidade.getAlunoId());

        return new MensalidadeDTO(
                mensalidade.getId(),
                mensalidade.getAlunoId(),
                aluno != null ? aluno.nome() : "Aluno não encontrado",
                mensalidade.getValorMensalidade(),
                mensalidade.getDataVencimento(),
                mensalidade.getStatus(),
                mensalidade.getValorPago(),
                mensalidade.getDataPagamento()
        );
    }
}
