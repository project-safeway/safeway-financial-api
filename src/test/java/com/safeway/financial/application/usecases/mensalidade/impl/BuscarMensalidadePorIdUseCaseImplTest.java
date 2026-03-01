package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.exceptions.AlunoNotFoundException;
import com.safeway.financial.domain.exceptions.MensalidadeNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarMensalidadePorIdUseCaseImplTest {

    @Mock
    private MensalidadeRepository mensalidadeRepository;

    @Mock
    private AlunoGateway alunoGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarMensalidadePorIdUseCaseImpl buscarMensalidadePorIdUseCase;

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
        @DisplayName("Deve buscar mensalidade por id com sucesso")
        void deveBuscarMensalidadePorIdComSucesso() {
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE);
            AlunoGateway.AlunoData alunoData = criarAlunoData(usuarioId);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscarPorId(mensalidadeId)).thenReturn(Optional.of(mensalidade));
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            MensalidadeDTO resultado = buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(mensalidadeId);
            assertThat(resultado.alunoId()).isEqualTo(alunoId);
            assertThat(resultado.nomeAluno()).isEqualTo("João Silva");
            assertThat(resultado.status()).isEqualTo(StatusPagamento.PENDENTE);
            verify(mensalidadeRepository).buscarPorId(mensalidadeId);
            verify(alunoGateway).buscarPorId(alunoId);
        }
    }

    @Nested
    @DisplayName("converterParaDomain")
    class ConverterParaDomain {

        @Test
        @DisplayName("Deve converter DTO para domain corretamente")
        void deveConverterDtoParaDomain() {
            MensalidadeDTO dto = new MensalidadeDTO(
                    mensalidadeId, alunoId, "João Silva", 500.0,
                    LocalDate.of(2026, 3, 15), StatusPagamento.PENDENTE, null, null
            );

            Mensalidade mensalidade = buscarMensalidadePorIdUseCase.converterParaDomain(dto);

            assertThat(mensalidade.getId()).isEqualTo(mensalidadeId);
            assertThat(mensalidade.getAlunoId()).isEqualTo(alunoId);
            assertThat(mensalidade.getValorMensalidade()).isEqualTo(500.0);
            assertThat(mensalidade.getDataVencimento()).isEqualTo(LocalDate.of(2026, 3, 15));
            assertThat(mensalidade.getStatus()).isEqualTo(StatusPagamento.PENDENTE);
            assertThat(mensalidade.getValorPago()).isNull();
            assertThat(mensalidade.getDataPagamento()).isNull();
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(mensalidadeRepository, never()).buscarPorId(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando mensalidade não é encontrada")
        void deveLancarExcecaoQuandoMensalidadeNaoEncontrada() {
            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscarPorId(mensalidadeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId))
                    .isInstanceOf(MensalidadeNotFoundException.class);

            verify(alunoGateway, never()).buscarPorId(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando aluno não é encontrado")
        void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscarPorId(mensalidadeId)).thenReturn(Optional.of(mensalidade));
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId))
                    .isInstanceOf(AlunoNotFoundException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário da sessão não corresponde ao usuário do aluno")
        void deveLancarExcecaoQuandoUsuarioNaoCorresponde() {
            UUID outroUsuarioId = UUID.randomUUID();
            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE);
            AlunoGateway.AlunoData alunoData = criarAlunoData(outroUsuarioId);

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscarPorId(mensalidadeId)).thenReturn(Optional.of(mensalidade));
            when(alunoGateway.buscarPorId(alunoId)).thenReturn(Optional.of(alunoData));

            assertThatThrownBy(() -> buscarMensalidadePorIdUseCase.buscarMensalidadePorId(mensalidadeId, usuarioId))
                    .isInstanceOf(OperationNotAlloyedException.class)
                    .hasMessageContaining("Operação não permitida");
        }
    }

    private Mensalidade criarMensalidade(StatusPagamento status) {
        return new Mensalidade(
                mensalidadeId, alunoId, LocalDate.now().plusDays(30),
                500.0, status, null, null
        );
    }

    private AlunoGateway.AlunoData criarAlunoData(UUID donorUsuarioId) {
        return new AlunoGateway.AlunoData(
                alunoId, "João Silva", 500.0, 15, true, donorUsuarioId
        );
    }
}

