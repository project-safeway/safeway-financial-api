package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoUseCase;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import com.safeway.financial.domain.specifications.PagamentoSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarPagamentoUseCaseImplTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarPagamentoUseCaseImpl buscarPagamentoUseCase;

    private UUID usuarioId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve buscar pagamentos com sucesso")
        void deveBuscarPagamentosComSucesso() {
            BuscarPagamentoUseCase.Input input = new BuscarPagamentoUseCase.Input(
                    usuarioId, null, null, null, null, null
            );
            Pagamento pagamento = criarPagamento();
            Page<Pagamento> pagamentosPage = new PageImpl<>(List.of(pagamento));

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscar(any(PagamentoSpecification.class), eq(pageable)))
                    .thenReturn(pagamentosPage);

            Page<PagamentoDTO> resultado = buscarPagamentoUseCase.buscarPagamentos(input, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().getFirst().usuarioId()).isEqualTo(usuarioId);
            assertThat(resultado.getContent().getFirst().valorPagamento()).isEqualTo(150.0);
            verify(pagamentoRepository).buscar(any(PagamentoSpecification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há pagamentos")
        void deveRetornarPaginaVazia() {
            BuscarPagamentoUseCase.Input input = new BuscarPagamentoUseCase.Input(
                    usuarioId, null, null, null, null, null
            );
            Page<Pagamento> paginaVazia = new PageImpl<>(Collections.emptyList());

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscar(any(PagamentoSpecification.class), eq(pageable)))
                    .thenReturn(paginaVazia);

            Page<PagamentoDTO> resultado = buscarPagamentoUseCase.buscarPagamentos(input, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Deve buscar pagamentos com filtros")
        void deveBuscarPagamentosComFiltros() {
            BuscarPagamentoUseCase.Input input = new BuscarPagamentoUseCase.Input(
                    usuarioId,
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31),
                    100.0,
                    500.0,
                    "luz"
            );
            Pagamento pagamento = criarPagamento();
            Page<Pagamento> pagamentosPage = new PageImpl<>(List.of(pagamento));

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscar(any(PagamentoSpecification.class), eq(pageable)))
                    .thenReturn(pagamentosPage);

            Page<PagamentoDTO> resultado = buscarPagamentoUseCase.buscarPagamentos(input, pageable);

            assertThat(resultado.getContent()).hasSize(1);
            verify(pagamentoRepository).buscar(any(PagamentoSpecification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve retornar múltiplos pagamentos")
        void deveRetornarMultiplosPagamentos() {
            BuscarPagamentoUseCase.Input input = new BuscarPagamentoUseCase.Input(
                    usuarioId, null, null, null, null, null
            );
            Pagamento p1 = criarPagamento();
            Pagamento p2 = new Pagamento(
                    UUID.randomUUID(), usuarioId, LocalDate.of(2026, 2, 15), 200.0, "Pagamento de água"
            );
            Page<Pagamento> pagamentosPage = new PageImpl<>(List.of(p1, p2));

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscar(any(PagamentoSpecification.class), eq(pageable)))
                    .thenReturn(pagamentosPage);

            Page<PagamentoDTO> resultado = buscarPagamentoUseCase.buscarPagamentos(input, pageable);

            assertThat(resultado.getContent()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            BuscarPagamentoUseCase.Input input = new BuscarPagamentoUseCase.Input(
                    usuarioId, null, null, null, null, null
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> buscarPagamentoUseCase.buscarPagamentos(input, pageable))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(pagamentoRepository, never()).buscar(any(), any());
        }
    }

    private Pagamento criarPagamento() {
        return new Pagamento(
                UUID.randomUUID(), usuarioId, LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
        );
    }
}

