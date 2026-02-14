package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarMensalidadesUseCaseImpl implements BuscarMensalidadesUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoGateway alunoGateway;

    @Override
    @Transactional(readOnly = true)
    public Page<MensalidadeDTO> executar(Input input, Pageable pageable) {
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

        //
//        Map<UUID, AlunoGateway.AlunoData> alunosCache = buscarAlunosEmLote(mensalidadesPage);
        Map<UUID, AlunoGateway.AlunoData> alunosCache = new HashMap<>();

        return mensalidadesPage.map(mensalidade -> converterParaDTO(mensalidade, alunosCache));
    }

    // TODO: Método precisa ser refatora pra funcionar de fato em lote. Agora ele ta SABOR lote
    private Map<UUID, AlunoGateway.AlunoData> buscarAlunosEmLote(Page<Mensalidade> mensalidadesPage) {
        Map<UUID, AlunoGateway.AlunoData> cache = new HashMap<>();

        mensalidadesPage.getContent().forEach(mensalidade -> {
            UUID alunoId = mensalidade.getAlunoId();

            if (!cache.containsKey(alunoId)) {
                alunoGateway.buscarPorId(alunoId)
                        .ifPresent(aluno -> cache.put(alunoId, aluno));
            }
        });

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
