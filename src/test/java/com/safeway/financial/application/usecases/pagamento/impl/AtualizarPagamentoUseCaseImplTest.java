package com.safeway.financial.application.usecases.pagamento.impl;

import com.safeway.financial.application.dto.PagamentoDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.pagamento.AtualizarPagamentoUseCase;
import com.safeway.financial.application.usecases.pagamento.BuscarPagamentoPorIdUseCase;
import com.safeway.financial.domain.entities.Pagamento;
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
class AtualizarPagamentoUseCaseImplTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private AtualizarPagamentoUseCaseImpl atualizarPagamentoUseCase;

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
        @DisplayName("Deve atualizar todos os campos do pagamento com sucesso")
        void deveAtualizarTodosCamposComSucesso() {
            AtualizarPagamentoUseCase.Input input = new AtualizarPagamentoUseCase.Input(
                    LocalDate.of(2026, 4, 1), 200.0, "Pagamento atualizado"
            );
            PagamentoDTO dtoExistente = criarPagamentoDTO();
            Pagamento pagamentoExistente = criarPagamento();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId)).thenReturn(dtoExistente);
            when(buscarPagamentoPorIdUseCase.converterParaDomain(dtoExistente)).thenReturn(pagamentoExistente);
            when(pagamentoRepository.salvar(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PagamentoDTO resultado = atualizarPagamentoUseCase.atualizarPagamento(pagamentoId, input, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.of(2026, 4, 1));
            assertThat(resultado.valorPagamento()).isEqualTo(200.0);
            assertThat(resultado.descricao()).isEqualTo("Pagamento atualizado");
            verify(pagamentoRepository).salvar(any(Pagamento.class));
        }

        @Test
        @DisplayName("Deve atualizar apenas a data de pagamento")
        void deveAtualizarApenasDataPagamento() {
            AtualizarPagamentoUseCase.Input input = new AtualizarPagamentoUseCase.Input(
                    LocalDate.of(2026, 5, 10), null, null
            );
            PagamentoDTO dtoExistente = criarPagamentoDTO();
            Pagamento pagamentoExistente = criarPagamento();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId)).thenReturn(dtoExistente);
            when(buscarPagamentoPorIdUseCase.converterParaDomain(dtoExistente)).thenReturn(pagamentoExistente);
            when(pagamentoRepository.salvar(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PagamentoDTO resultado = atualizarPagamentoUseCase.atualizarPagamento(pagamentoId, input, usuarioId);

            assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.of(2026, 5, 10));
            assertThat(resultado.valorPagamento()).isEqualTo(150.0);
            assertThat(resultado.descricao()).isEqualTo("Pagamento de luz");
        }

        @Test
        @DisplayName("Deve atualizar apenas o valor de pagamento")
        void deveAtualizarApenasValorPagamento() {
            AtualizarPagamentoUseCase.Input input = new AtualizarPagamentoUseCase.Input(
                    null, 300.0, null
            );
            PagamentoDTO dtoExistente = criarPagamentoDTO();
            Pagamento pagamentoExistente = criarPagamento();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId)).thenReturn(dtoExistente);
            when(buscarPagamentoPorIdUseCase.converterParaDomain(dtoExistente)).thenReturn(pagamentoExistente);
            when(pagamentoRepository.salvar(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PagamentoDTO resultado = atualizarPagamentoUseCase.atualizarPagamento(pagamentoId, input, usuarioId);

            assertThat(resultado.valorPagamento()).isEqualTo(300.0);
            assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(resultado.descricao()).isEqualTo("Pagamento de luz");
        }

        @Test
        @DisplayName("Deve atualizar apenas a descrição")
        void deveAtualizarApenasDescricao() {
            AtualizarPagamentoUseCase.Input input = new AtualizarPagamentoUseCase.Input(
                    null, null, "Nova descrição"
            );
            PagamentoDTO dtoExistente = criarPagamentoDTO();
            Pagamento pagamentoExistente = criarPagamento();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId)).thenReturn(dtoExistente);
            when(buscarPagamentoPorIdUseCase.converterParaDomain(dtoExistente)).thenReturn(pagamentoExistente);
            when(pagamentoRepository.salvar(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PagamentoDTO resultado = atualizarPagamentoUseCase.atualizarPagamento(pagamentoId, input, usuarioId);

            assertThat(resultado.descricao()).isEqualTo("Nova descrição");
            assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(resultado.valorPagamento()).isEqualTo(150.0);
        }

        @Test
        @DisplayName("Não deve alterar nenhum campo quando todos os inputs são null")
        void naoDeveAlterarQuandoTodosInputsNull() {
            AtualizarPagamentoUseCase.Input input = new AtualizarPagamentoUseCase.Input(
                    null, null, null
            );
            PagamentoDTO dtoExistente = criarPagamentoDTO();
            Pagamento pagamentoExistente = criarPagamento();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarPagamentoPorIdUseCase.buscarPagamentoPorId(pagamentoId, usuarioId)).thenReturn(dtoExistente);
            when(buscarPagamentoPorIdUseCase.converterParaDomain(dtoExistente)).thenReturn(pagamentoExistente);
            when(pagamentoRepository.salvar(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PagamentoDTO resultado = atualizarPagamentoUseCase.atualizarPagamento(pagamentoId, input, usuarioId);

            assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(resultado.valorPagamento()).isEqualTo(150.0);
            assertThat(resultado.descricao()).isEqualTo("Pagamento de luz");
            verify(pagamentoRepository).salvar(any(Pagamento.class));
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            AtualizarPagamentoUseCase.Input input = new AtualizarPagamentoUseCase.Input(
                    LocalDate.of(2026, 4, 1), 200.0, "Atualizado"
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> atualizarPagamentoUseCase.atualizarPagamento(pagamentoId, input, usuarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(pagamentoRepository, never()).salvar(any());
        }
    }

    private PagamentoDTO criarPagamentoDTO() {
        return new PagamentoDTO(
                pagamentoId, usuarioId, LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
        );
    }

    private Pagamento criarPagamento() {
        return new Pagamento(
                pagamentoId, usuarioId, LocalDate.of(2026, 3, 1), 150.0, "Pagamento de luz"
        );
    }
}

