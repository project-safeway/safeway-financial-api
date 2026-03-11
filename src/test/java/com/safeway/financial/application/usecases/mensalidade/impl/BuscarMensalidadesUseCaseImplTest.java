package com.safeway.financial.application.usecases.mensalidade.impl;

import com.safeway.financial.application.dto.MensalidadeDTO;
import com.safeway.financial.application.ports.output.AlunoGateway;
import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.application.usecases.mensalidade.BuscarMensalidadesUseCase;
import com.safeway.financial.domain.entities.Mensalidade;
import com.safeway.financial.domain.enums.StatusPagamento;
import com.safeway.financial.domain.repositories.MensalidadeRepository;
import com.safeway.financial.domain.specifications.MensalidadeSpecification;
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
class BuscarMensalidadesUseCaseImplTest {

    @Mock
    private MensalidadeRepository mensalidadeRepository;

    @Mock
    private AlunoGateway alunoGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarMensalidadesUseCaseImpl buscarMensalidadesUseCase;

    private UUID usuarioId;
    private UUID alunoId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve buscar mensalidades com sucesso")
        void deveBuscarMensalidadesComSucesso() {
            BuscarMensalidadesUseCase.Input input = new BuscarMensalidadesUseCase.Input(
                    alunoId, null, null, null, usuarioId
            );

            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE);
            Page<Mensalidade> mensalidadesPage = new PageImpl<>(List.of(mensalidade));
            AlunoGateway.AlunoData alunoData = criarAlunoData();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscar(any(MensalidadeSpecification.class), eq(pageable)))
                    .thenReturn(mensalidadesPage);
            when(alunoGateway.buscarPorIdEmLote(anyList())).thenReturn(List.of(alunoData));

            Page<MensalidadeDTO> resultado = buscarMensalidadesUseCase.executar(input, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().getFirst().nomeAluno()).isEqualTo("João Silva");
            assertThat(resultado.getContent().getFirst().status()).isEqualTo(StatusPagamento.PENDENTE);
            verify(mensalidadeRepository).buscar(any(MensalidadeSpecification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há mensalidades")
        void deveRetornarPaginaVaziaQuandoNaoHaMensalidades() {
            BuscarMensalidadesUseCase.Input input = new BuscarMensalidadesUseCase.Input(
                    alunoId, null, null, null, usuarioId
            );

            Page<Mensalidade> paginaVazia = new PageImpl<>(Collections.emptyList());

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscar(any(MensalidadeSpecification.class), eq(pageable)))
                    .thenReturn(paginaVazia);
            when(alunoGateway.buscarPorIdEmLote(anyList())).thenReturn(Collections.emptyList());

            Page<MensalidadeDTO> resultado = buscarMensalidadesUseCase.executar(input, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Deve buscar mensalidades com filtros de data e status")
        void deveBuscarMensalidadesComFiltros() {
            BuscarMensalidadesUseCase.Input input = new BuscarMensalidadesUseCase.Input(
                    alunoId,
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31),
                    List.of(StatusPagamento.PENDENTE, StatusPagamento.ATRASADO),
                    usuarioId
            );

            Mensalidade m1 = criarMensalidade(StatusPagamento.PENDENTE);
            Mensalidade m2 = new Mensalidade(
                    UUID.randomUUID(), alunoId, "João Silva", LocalDate.of(2026, 2, 15),
                    300.0, StatusPagamento.ATRASADO, null, null
            );
            Page<Mensalidade> mensalidadesPage = new PageImpl<>(List.of(m1, m2));
            AlunoGateway.AlunoData alunoData = criarAlunoData();

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscar(any(MensalidadeSpecification.class), eq(pageable)))
                    .thenReturn(mensalidadesPage);
            when(alunoGateway.buscarPorIdEmLote(anyList())).thenReturn(List.of(alunoData));

            Page<MensalidadeDTO> resultado = buscarMensalidadesUseCase.executar(input, pageable);

            assertThat(resultado.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("Deve retornar 'Aluno não encontrado' quando aluno não está no cache")
        void deveRetornarAlunoNaoEncontradoQuandoNaoEstaNoCache() {
            BuscarMensalidadesUseCase.Input input = new BuscarMensalidadesUseCase.Input(
                    alunoId, null, null, null, usuarioId
            );

            Mensalidade mensalidade = criarMensalidade(StatusPagamento.PENDENTE);
            Page<Mensalidade> mensalidadesPage = new PageImpl<>(List.of(mensalidade));

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(true);
            when(mensalidadeRepository.buscar(any(MensalidadeSpecification.class), eq(pageable)))
                    .thenReturn(mensalidadesPage);
            when(alunoGateway.buscarPorIdEmLote(anyList())).thenReturn(Collections.emptyList());

            Page<MensalidadeDTO> resultado = buscarMensalidadesUseCase.executar(input, pageable);

            assertThat(resultado.getContent().getFirst().nomeAluno()).isEqualTo("Aluno não encontrado");
        }
    }

    @Nested
    @DisplayName("Cenários de falha")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            BuscarMensalidadesUseCase.Input input = new BuscarMensalidadesUseCase.Input(
                    alunoId, null, null, null, usuarioId
            );

            when(usuarioGateway.estaAtivo(usuarioId)).thenReturn(false);

            assertThatThrownBy(() -> buscarMensalidadesUseCase.executar(input, pageable))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Usuário inativo");

            verify(mensalidadeRepository, never()).buscar(any(), any());
        }
    }

    private Mensalidade criarMensalidade(StatusPagamento status) {
        return new Mensalidade(
                UUID.randomUUID(), alunoId, "João Silva", LocalDate.now().plusDays(30),
                500.0, status, null, null
        );
    }

    private AlunoGateway.AlunoData criarAlunoData() {
        return new AlunoGateway.AlunoData(
                alunoId, "João Silva", 500.0, 15, true, usuarioId
        );
    }
}

