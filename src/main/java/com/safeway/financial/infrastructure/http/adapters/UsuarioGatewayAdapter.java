package com.safeway.financial.infrastructure.http.adapters;

import com.safeway.financial.application.ports.output.UsuarioGateway;
import com.safeway.financial.infrastructure.http.clients.UsuarioClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioGatewayAdapter implements UsuarioGateway {

    private final UsuarioClient usuarioClient;

    @Override
    public Optional<UsuarioData> buscarPorId(UUID id) {
        try {
            log.debug("Buscando dados do usuário {} no serviço principal", id);

            UsuarioClient.UsuarioResponse response = usuarioClient.buscarUsuario(id);

            UsuarioData usuarioData = new UsuarioData(
                    response.id(),
                    response.nome(),
                    response.email(),
                    response.ativo()
            );

            return Optional.of(usuarioData);
        } catch (FeignException.NotFound ex) {
            log.warn("Usuário {} não encontrado no serviço principal", id);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Erro ao buscar usuário {} no serviço principal", id, ex);
            throw new RuntimeException("Erro ao comunicar com o serviço principal", ex);
        }
    }

    @Override
    public boolean estaAtivo(UUID id) {
        return buscarPorId(id).map(UsuarioData::ativo).orElse(false);
    }
}
