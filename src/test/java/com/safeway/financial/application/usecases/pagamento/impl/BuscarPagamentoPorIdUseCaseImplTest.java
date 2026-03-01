package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.exceptions.PagamentoNotFoundException;
import com.safeway.financial.domain.repositories.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarPagamentoPorIdUseCaseImplTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarPagamentoPorIdUseCaseImpl buscarPagamentoPorIdUseCase;

    private UUID pagamentoId;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        pagamentoId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve buscar pagamento por id com sucesso")
        void deveBuscarPagamentoPorIdComSucesso() {
            Pagamento pagamento = criarPagamento(usuarioId);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento));

            PagamentoDTO resultado = buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(pagamentoId);
            assertThat(resultado.usuarioId()).isEqualTo(usuarioId);
            assertThat(resultado.valorPagamento()).isEqualTo(150.0);
            assertThat(resultado.descricao()).isEqualTo("Pagamento de luz");
            verify(pagamentoRepository).buscarPorId(pagamentoId);
        }
    }

    @Nested
    @DisplayName("converterParaDomain")
    class ConverterParaDomain {

        @Test
        @DisplayName("Deve converter DTO para domain corretamente")
        void deveConverterDtoParaDomain() {
            PagamentoDTO dto = new PagamentoDTO(
                    pagamentoId, usuarioId, LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
            );

            Pagamento pagamento = buscarPagamentoPorIdUseCase.converterParaDomain(dto);

            assertThat(pagamento.getId()).isEqualTo(pagamentoId);
            assertThat(pagamento.getUsuarioId()).isEqualTo(usuarioId);
            assertThat(pagamento.getDataPagamento()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(pagamento.getValorPagamento()).isEqualTo(150.0);
            assertThat(pagamento.getDescricao()).isEqualTo("Pagamento de luz");
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(pagamentoRepository, never()).buscarPorId(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando pagamento não é encontrado")
        void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscarPorId(pagamentoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId))
                    .isInstanceOf(PagamentoNotFoundException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando pagamento não pertence ao usuário")
        void deveLancarExcecaoQuandoPagamentoNaoPertenceAoUsuario() {
            UUID outroUsuarioId = UUID.randomUUID();
            Pagamento pagamento = criarPagamento(outroUsuarioId);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento));

            assertThatThrownBy(() -> buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId))
                    .isInstanceOf(PagamentoNotFoundException.class);
        }
    }

    private Pagamento criarPagamento(UUID donoUsuarioId) {
        return new Pagamento(
                pagamentoId, donoUsuarioId, LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
        );
    }
}


