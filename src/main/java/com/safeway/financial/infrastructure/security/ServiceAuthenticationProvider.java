package com.safeway.financial.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceAuthenticationProvider {

    private final RestTemplate restTemplate;

    @Value("${safeway.core.url}")
    private String coreUrl;

    @Value("${safeway.service.auth.username:servico-financeiro@safeway.internal}")
    private String serviceUsername;

    @Value("${safeway.service.auth.password}")
    private String servicePassword;

    private String cachedToken;
    private Instant tokenExpiration;

    public String getServiceToken() {
        if (cachedToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            log.debug("Usando token em cache válido");
            return cachedToken;
        }

        log.info("Obtendo novo token de serviço");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> loginRequest = Map.of(
                "email", serviceUsername,
                "senha", servicePassword
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);

            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                    coreUrl + "/api/v1/auth/login",
                    request,
                    LoginResponse.class
            );

            if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
                cachedToken = response.getBody().accessToken();
                tokenExpiration = Instant.now().plusSeconds(response.getBody().expiresIn());
                log.info("Novo token obtido com sucesso, expira em: {}", tokenExpiration);
                return cachedToken;
            }

            throw new RuntimeException("Falha ao obter token de serviço: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Erro ao autenticar serviço: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao autenticar serviço", e);
        }
    }

    public void invalidateToken() {
        cachedToken = null;
        tokenExpiration = null;
    }

    record LoginResponse(String accessToken, Long expiresIn, String nomeUsuario, Long idTransporte) {
    }
}
