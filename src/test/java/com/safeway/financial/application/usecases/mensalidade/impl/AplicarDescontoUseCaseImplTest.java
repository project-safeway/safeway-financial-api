package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import com.safeway.financial.domain.exceptions.ValorDescontoNotValidException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AplicarDescontoUseCaseImplTest {

    @Mock
    private MensalidadeRepository mensalidadeRepository;

    @Mock
    private BuscarMensalidadePorIdUseCase buscarMensalidadePorIdUseCase;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private AplicarDescontoUseCaseImpl aplicarDescontoUseCase;

    private UUID mensalidadeId;
    private UUID usuarioId;
    private UUID alunoId;

    @BeforeEach
    void setUp() {
        mensalidadeId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve aplicar desconto com sucesso")
        void deveAplicarDescontoComSucesso() {
            Double valorDesconto = 50.0;
            MensalidadeDTO dto = criarMensalidadeDTO(500.0, StatusPagamento.PENDENTE, null, null);
            Mensalidade mensalidade = criarMensalidade(500.0, StatusPagamento.PENDENTE, null, null);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);
            when(mensalidadeRepository.salvar(any(Mensalidade.class))).thenReturn(mensalidade);

            MensalidadeDTO resultado = aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, valorDesconto, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.valorMensalidade()).isEqualTo(450.0);
            verify(mensalidadeRepository).salvar(any(Mensalidade.class));
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, 50.0, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(mensalidadeRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor do desconto é null")
        void deveLancarExcecaoQuandoValorDescontoNull() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, null, usuarioId))
                    .isInstanceOf(ValorDescontoNotValidException.class)
                    .hasMessageContaining("maior que zero");

            verify(mensalidadeRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor do desconto é zero")
        void deveLancarExcecaoQuandoValorDescontoZero() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, 0.0, usuarioId))
                    .isInstanceOf(ValorDescontoNotValidException.class)
                    .hasMessageContaining("maior que zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor do desconto é negativo")
        void deveLancarExcecaoQuandoValorDescontoNegativo() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);

            assertThatThrownBy(() -> aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, -10.0, usuarioId))
                    .isInstanceOf(ValorDescontoNotValidException.class)
                    .hasMessageContaining("maior que zero");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor do desconto é maior ou igual ao valor da mensalidade")
        void deveLancarExcecaoQuandoDescontoMaiorOuIgualAoValor() {
            Double valorDesconto = 500.0;
            MensalidadeDTO dto = criarMensalidadeDTO(500.0, StatusPagamento.PENDENTE, null, null);
            Mensalidade mensalidade = criarMensalidade(500.0, StatusPagamento.PENDENTE, null, null);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);

            assertThatThrownBy(() -> aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, valorDesconto, usuarioId))
                    .isInstanceOf(ValorDescontoNotValidException.class)
                    .hasMessageContaining("maior ou igual ao valor da mensalidade");

            verify(mensalidadeRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor do desconto é maior que o valor da mensalidade")
        void deveLancarExcecaoQuandoDescontoMaiorQueValor() {
            Double valorDesconto = 600.0;
            MensalidadeDTO dto = criarMensalidadeDTO(500.0, StatusPagamento.PENDENTE, null, null);
            Mensalidade mensalidade = criarMensalidade(500.0, StatusPagamento.PENDENTE, null, null);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);

            assertThatThrownBy(() -> aplicarDescontoUseCase.aplicarDesconto(mensalidadeId, valorDesconto, usuarioId))
                    .isInstanceOf(ValorDescontoNotValidException.class);
        }
    }

    private MensalidadeDTO criarMensalidadeDTO(Double valor, StatusPagamento status, Double valorPago, LocalDate dataPagamento) {
        return new MensalidadeDTO(
                mensalidadeId, alunoId, "João Silva", valor,
                LocalDate.now().plusDays(30), status, valorPago, dataPagamento
        );
    }

    private Mensalidade criarMensalidade(Double valor, StatusPagamento status, Double valorPago, LocalDate dataPagamento) {
        return new Mensalidade(
                mensalidadeId, alunoId, "João Silva", LocalDate.now().plusDays(30),
                valor, status, dataPagamento, valorPago
        );
    }
}

