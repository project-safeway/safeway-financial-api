package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.pagamento.RegistrarPagamentoUseCase;
import com.safeway.financial.domain.entities.Pagamento;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarPagamentoUseCaseImplTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private RegistrarPagamentoUseCaseImpl registrarPagamentoUseCase;

    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve registrar novo pagamento com sucesso")
        void deveRegistrarNovoPagamentoComSucesso() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
            );
            Pagamento pagamentoSalvo = new Pagamento(
                    UUID.randomUUID(), usuarioId, input.dataPagamento(),
                    input.valorPagamento(), input.descricao()
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(pagamentoRepository.salvar(any(Pagamento.class))).thenReturn(pagamentoSalvo);

            PagamentoDTO resultado = registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.usuarioId()).isEqualTo(usuarioId);
            assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(resultado.valorPagamento()).isEqualTo(150.0);
            assertThat(resultado.descricao()).isEqualTo("Pagamento de luz");
            verify(pagamentoRepository).salvar(any(Pagamento.class));
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Usuário")
    class FalhaUsuario {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            RegistrarPagamentoUseCase.Input input = criarInputValido();
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(pagamentoRepository, never()).salvar(any());
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Validação de estrutura")
    class FalhaValidacao {

        @Test
        @DisplayName("Deve lançar exceção quando data de pagamento é null")
        void deveLancarExcecaoQuandoDataPagamentoNull() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    null, 150.0, "Pagamento de luz"
            );
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("data de pagamento é obrigatória");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor de pagamento é null")
        void deveLancarExcecaoQuandoValorPagamentoNull() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    LocalDate.of(2026, 3, 1), null, "Pagamento de luz"
            );
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("valor de pagamento deve ser maior que zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor de pagamento é zero")
        void deveLancarExcecaoQuandoValorPagamentoZero() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    LocalDate.of(2026, 3, 1), 0.0, "Pagamento de luz"
            );
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("valor de pagamento deve ser maior que zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor de pagamento é negativo")
        void deveLancarExcecaoQuandoValorPagamentoNegativo() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    LocalDate.of(2026, 3, 1), -50.0, "Pagamento de luz"
            );
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("valor de pagamento deve ser maior que zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição é null")
        void deveLancarExcecaoQuandoDescricaoNull() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    LocalDate.of(2026, 3, 1), 150.0, null
            );
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("descrição do pagamento é obrigatória");
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição é vazia")
        void deveLancarExcecaoQuandoDescricaoVazia() {
            RegistrarPagamentoUseCase.Input input = new RegistrarPagamentoUseCase.Input(
                    LocalDate.of(2026, 3, 1), 150.0, "   "
            );
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> registrarPagamentoUseCase.registrarNovoPagamento(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("descrição do pagamento é obrigatória");
        }
    }

    private RegistrarPagamentoUseCase.Input criarInputValido() {
        return new RegistrarPagamentoUseCase.Input(
                LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
        );
    }
}

