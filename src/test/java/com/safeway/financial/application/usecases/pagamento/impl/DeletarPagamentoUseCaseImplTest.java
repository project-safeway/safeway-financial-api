package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoPorIdUseCase;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletarPagamentoUseCaseImplTest {

    @Mock
    private BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private DeletarPagamentoUseCaseImpl deletarPagamentoUseCase;

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
        @DisplayName("Deve deletar pagamento com sucesso")
        void deveDeletarPagamentoComSucesso() {
            PagamentoDTO dto = new PagamentoDTO(
                    pagamentoId, usuarioId, LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId)).thenReturn(dto);

            assertThatCode(() -> deletarPagamentoUseCase.deletarPagamento(pagamentoId, usuarioId))
                    .doesNotThrowAnyException();

            verify(buscarPagamentoPorIdUseCase).buscarPagamentoPorId(pagamentoId, usuarioId);
            verify(pagamentoRepository).deletar(pagamentoId);
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> deletarPagamentoUseCase.deletarPagamento(pagamentoId, usuarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(pagamentoRepository, never()).deletar(any());
        }

        @Test
        @DisplayName("Deve propagar exceção quando buscarPagamentoPorId falha")
        void devePropagarExcecaoQuandoBuscarFalha() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId))
                    .thenThrow(new RuntimeException("Erro ao tentar buscar o pagamento"));

            assertThatThrownBy(() -> deletarPagamentoUseCase.deletarPagamento(pagamentoId, usuarioId))
                    .isInstanceOf(RuntimeException.class);

            verify(pagamentoRepository, never()).deletar(any());
        }
    }
}

