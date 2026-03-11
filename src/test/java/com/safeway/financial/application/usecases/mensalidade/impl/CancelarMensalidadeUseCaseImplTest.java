package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadePorIdUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.MensalidadeWithFinalStatusException;
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
class CancelarMensalidadeUseCaseImplTest {

    @Mock
    private MensalidadeRepository mensalidadeRepository;

    @Mock
    private BuscarMensalidadePorIdUseCase buscarMensalidadePorIdUseCase;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private CancelarMensalidadeUseCaseImpl cancelarMensalidadeUseCase;

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
        @DisplayName("Deve cancelar mensalidade pendente com sucesso")
        void deveCancelarMensalidadePendenteComSucesso() {
            MensalidadeDTO dto = criarMensalidadeDTO(StatusPagamento.PENDENTE, null, null);
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE, null, null);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);
            when(mensalidadeRepository.salvar(any(Mensalidade.class))).thenReturn(mensalidade);

            MensalidadeDTO resultado = cancelarMensalidadeUseCase.cancelarMensalidade(mensalidadeId, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusPagamento.CANCELADO);
            verify(mensalidadeRepository).salvar(any(Mensalidade.class));
        }

        @Test
        @DisplayName("Deve cancelar mensalidade atrasada com sucesso")
        void deveCancelarMensalidadeAtrasadaComSucesso() {
            MensalidadeDTO dto = criarMensalidadeDTO(StatusPagamento.ATRASADO, null, null);
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.ATRASADO, null, null);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);
            when(mensalidadeRepository.salvar(any(Mensalidade.class))).thenReturn(mensalidade);

            MensalidadeDTO resultado = cancelarMensalidadeUseCase.cancelarMensalidade(mensalidadeId, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusPagamento.CANCELADO);
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> cancelarMensalidadeUseCase.cancelarMensalidade(mensalidadeId, usuarioId))
                    .isInstanceOf(MensalidadeWithFinalStatusException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(mensalidadeRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando mensalidade já está paga")
        void deveLancarExcecaoQuandoMensalidadeJaPaga() {
            MensalidadeDTO dto = criarMensalidadeDTO(StatusPagamento.PAGO, 500.0, LocalDate.now());
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PAGO, 500.0, LocalDate.now());

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);

            assertThatThrownBy(() -> cancelarMensalidadeUseCase.cancelarMensalidade(mensalidadeId, usuarioId))
                    .isInstanceOf(MensalidadeWithFinalStatusException.class)
                    .hasMessageContaining("já se encontra paga");

            verify(mensalidadeRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando mensalidade possui data de pagamento")
        void deveLancarExcecaoQuandoMensalidadePossuiDataPagamento() {
            MensalidadeDTO dto = criarMensalidadeDTO(StatusPagamento.PENDENTE, null, LocalDate.now());
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE, null, LocalDate.now());

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);

            assertThatThrownBy(() -> cancelarMensalidadeUseCase.cancelarMensalidade(mensalidadeId, usuarioId))
                    .isInstanceOf(MensalidadeWithFinalStatusException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando mensalidade já está cancelada")
        void deveLancarExcecaoQuandoMensalidadeJaCancelada() {
            MensalidadeDTO dto = criarMensalidadeDTO(StatusPagamento.CANCELADO, null, null);
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.CANCELADO, null, null);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId)).thenReturn(dto);
            when(buscarMensalidadePorIdUseCase.converterParaDomain(dto)).thenReturn(mensalidade);

            assertThatThrownBy(() -> cancelarMensalidadeUseCase.cancelarMensalidade(mensalidadeId, usuarioId))
                    .isInstanceOf(MensalidadeWithFinalStatusException.class)
                    .hasMessageContaining("já cancelada");
        }
    }

    private MensalidadeDTO criarMensalidadeDTO(StatusPagamento status, Double valorPago, LocalDate dataPagamento) {
        return new MensalidadeDTO(
                mensalidadeId, alunoId, "João Silva", 500.0,
                LocalDate.now().plusDays(30), status, valorPago, dataPagamento
        );
    }

    private Mensalidade criarMensalidade(StatusPagamento status, Double valorPago, LocalDate dataPagamento) {
        return new Mensalidade(
                mensalidadeId, alunoId, "João Silva", LocalDate.now().plusDays(30),
                500.0, status, dataPagamento, valorPago
        );
    }
}

