package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.CriarMensalidadeUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.AlunoNotFoundException;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarMensalidadeUseCaseImplTest {

    @Mock
    private MensalidadeRepository mensalidadeRepository;

    @Mock
    private AlunoGateway alunoGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private CriarMensalidadeUseCaseImpl criarMensalidadeUseCase;

    private UUID usuarioId;
    private UUID alunoId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve criar mensalidade pendente com sucesso")
        void deveCriarMensalidadePendenteComSucesso() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PENDENTE, null, null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            Mensalidade mensalidadeSalva = new Mensalidade(
                    UUID.randomUUID(), alunoId, "João Silva", input.dataVencimento(),
                    input.valorMensalidade(), input.status(), null, null
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));
            when(mensalidadeRepository.salvar(any(Mensalidade.class))).thenReturn(mensalidadeSalva);

            MensalidadeDTO resultado = criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.valorMensalidade()).isEqualTo(500.0);
            assertThat(resultado.status()).isEqualTo(StatusPagamento.PENDENTE);
            assertThat(resultado.nomeAluno()).isEqualTo("João Silva");
            verify(mensalidadeRepository).salvar(any(Mensalidade.class));
        }

        @Test
        @DisplayName("Deve criar mensalidade paga com sucesso")
        void deveCriarMensalidadePagaComSucesso() {
            LocalDate dataPagamento = LocalDate.now();
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PAGO, dataPagamento, 500.0
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            Mensalidade mensalidadeSalva = new Mensalidade(
                    UUID.randomUUID(), alunoId, "João Silva", input.dataVencimento(),
                    input.valorMensalidade(), input.status(), dataPagamento, 500.0
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));
            when(mensalidadeRepository.salvar(any(Mensalidade.class))).thenReturn(mensalidadeSalva);

            MensalidadeDTO resultado = criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusPagamento.PAGO);
            assertThat(resultado.valorPago()).isEqualTo(500.0);
            assertThat(resultado.dataPagamento()).isEqualTo(dataPagamento);
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Usuário")
    class FalhaUsuario {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            CriarMensalidadeUseCase.Input input = criarInputPendente();
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class);
            verify(mensalidadeRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não tem permissão")
        void deveLancarExcecaoQuandoUsuarioSemPermissao() {
            UUID outroUsuarioId = UUID.randomUUID();
            CriarMensalidadeUseCase.Input input = criarInputPendente();
            AlunoGateway.AlunoData alunoData = new AlunoGateway.AlunoData(
                    alunoId, "João Silva", 500.0, 15, true, outroUsuarioId
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class);
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Aluno")
    class FalhaAluno {

        @Test
        @DisplayName("Deve lançar exceção quando aluno não é encontrado")
        void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
            CriarMensalidadeUseCase.Input input = criarInputPendente();
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(AlunoNotFoundException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando aluno está inativo")
        void deveLancarExcecaoQuandoAlunoInativo() {
            CriarMensalidadeUseCase.Input input = criarInputPendente();
            AlunoGateway.AlunoData alunoData = criarAlunoData(false);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class);
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Validação de estrutura")
    class FalhaValidacaoEstrutura {

        @Test
        @DisplayName("Deve criar mensalidade com status PENDENTE quando status é null")
        void deveCriarMensalidadeComStatusPendenteQuandoStatusNull() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0, null, null, null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            Mensalidade mensalidadeSalva = new Mensalidade(
                    UUID.randomUUID(), alunoId, "João Silva", input.dataVencimento(),
                    input.valorMensalidade(), StatusPagamento.PENDENTE, null, null
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));
            when(mensalidadeRepository.salvar(any(Mensalidade.class))).thenReturn(mensalidadeSalva);

            MensalidadeDTO resultado = criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusPagamento.PENDENTE);
            verify(mensalidadeRepository).salvar(any(Mensalidade.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data de vencimento é null")
        void deveLancarExcecaoQuandoDataVencimentoNull() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, null, 500.0, StatusPagamento.PENDENTE, null, null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Data de vencimento é obrigatória");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor da mensalidade é null")
        void deveLancarExcecaoQuandoValorMensalidadeNull() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), null, StatusPagamento.PENDENTE, null, null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Valor da mensalidade deve ser maior que zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor da mensalidade é zero")
        void deveLancarExcecaoQuandoValorMensalidadeZero() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 0.0, StatusPagamento.PENDENTE, null, null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Validação de coerência")
    class FalhaValidacaoCoerencia {

        @Test
        @DisplayName("Deve lançar exceção quando valor pago é negativo")
        void deveLancarExcecaoQuandoValorPagoNegativo() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PAGO, LocalDate.now(), -10.0
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Valor pago não pode ser negativo");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor pago é maior que o valor da mensalidade")
        void deveLancarExcecaoQuandoValorPagoMaiorQueValor() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PAGO, LocalDate.now(), 600.0
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Valor pago não pode ser maior que o valor da mensalidade");
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Regras de negócio por status")
    class FalhaRegrasNegocio {

        @Test
        @DisplayName("Deve lançar exceção quando status PAGO sem dataPagamento")
        void deveLancarExcecaoQuandoPagoSemDataPagamento() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PAGO, null, 500.0
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("dataPagamento e valorPago são obrigatórios");
        }

        @Test
        @DisplayName("Deve lançar exceção quando status PAGO sem valorPago")
        void deveLancarExcecaoQuandoPagoSemValorPago() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PAGO, LocalDate.now(), null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("dataPagamento e valorPago são obrigatórios");
        }

        @Test
        @DisplayName("Deve lançar exceção quando status PENDENTE com dataPagamento")
        void deveLancarExcecaoQuandoPendenteComDataPagamento() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PENDENTE, LocalDate.now(), null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não deve existir pagamento");
        }

        @Test
        @DisplayName("Deve lançar exceção quando status PENDENTE com valorPago")
        void deveLancarExcecaoQuandoPendenteComValorPago() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.PENDENTE, null, 200.0
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não deve existir pagamento");
        }

        @Test
        @DisplayName("Deve lançar exceção quando status ATRASADO com dataPagamento")
        void deveLancarExcecaoQuandoAtrasadoComDataPagamento() {
            CriarMensalidadeUseCase.Input input = new CriarMensalidadeUseCase.Input(
                    alunoId, LocalDate.of(2026, 4, 15), 500.0,
                    StatusPagamento.ATRASADO, LocalDate.now(), null
            );
            AlunoGateway.AlunoData alunoData = criarAlunoData(true);
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> criarMensalidadeUseCase.criarNovaMensalidade(input, usuarioId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não deve existir pagamento");
        }
    }

    private CriarMensalidadeUseCase.Input criarInputPendente() {
        return new CriarMensalidadeUseCase.Input(
                alunoId, LocalDate.of(2026, 4, 15), 500.0,
                StatusPagamento.PENDENTE, null, null
        );
    }

    private AlunoGateway.AlunoData criarAlunoData(boolean ativo) {
        return new AlunoGateway.AlunoData(
                alunoId, "João Silva", 500.0, 15, ativo, usuarioId
        );
    }
}

