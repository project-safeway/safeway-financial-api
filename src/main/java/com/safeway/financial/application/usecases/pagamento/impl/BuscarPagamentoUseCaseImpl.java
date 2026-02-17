package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoUseCase;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import com.safeway.financial.domain.specifications.PagamentoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarPagamentoUseCaseImpl implements BuscarPagamentoUseCase {

    private final PagamentoRepository pagamentoRepository;

    @Override
    public Page<PagamentoDTO> buscarPagamentos(Input input, Pageable pageable) {
        log.info("Buscando pagamentos - Filtros: {}, Usuário: {}", input, input.usuarioId());

        PagamentoSpecification spec = PagamentoSpecification.builder()
                .usuarioId(input.usuarioId())
                .dataInicio(input.dataInicio())
                .dataFim(input.dataFim())
                .valorMinimo(input.valorMinimo())
                .valorMaximo(input.valorMaximo())
                .descricao(input.descricao())
                .build();

        Page<Pagamento> pagamentosPage = pagamentoRepository.buscar(spec, pageable);

        log.info("Encontrados {} pagamentos (total: {})",
                pagamentosPage.getNumberOfElements(),
                pagamentosPage.getTotalElements());

        return pagamentosPage.map(this::converterParaDTO);
    }

    private PagamentoDTO converterParaDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getUsuarioId(),
                pagamento.getDataPagamento(),
                pagamento.getValorPagamento(),
                pagamento.getDescricao()
        );
    }

}
