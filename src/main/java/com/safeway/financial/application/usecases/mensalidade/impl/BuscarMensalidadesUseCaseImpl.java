package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.mappers.MensalidadeApplicationMapper;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadesUseCase;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarMensalidadesUseCaseImpl implements BuscarMensalidadesUseCase {

    private final MensalidadeRepository mensalidadeRepository;
    private final MensalidadeApplicationMapper mapper;

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

        Page<MensalidadeDTO> resultado = mensalidadeRepository.buscar(spec, pageable)
                .map(mapper::toDTO);

        log.info("Encontradas {} mensalidades (total: {})",
                resultado.getNumberOfElements(),
                resultado.getTotalElements());

        return resultado;
    }
}
