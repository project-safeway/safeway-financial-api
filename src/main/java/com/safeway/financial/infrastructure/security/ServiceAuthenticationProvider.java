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

    @Value("${auth.service.base-url:http://localhost:8080}")
    private String authServiceBaseUrl;

    @Value("${auth.service.token-endpoint:/auth/v2/token/service}")
    private String authServiceTokenEndpoint;

    @Value("${auth.service.client-id:safeway-core}")
    private String authServiceClientId;

    @Value("${auth.service.client-secret:change-me}")
    private String authServiceClientSecret;

    private String cachedToken;
    private Instant tokenExpiration;

    public String getServiceToken() {
        if (cachedToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            log.debug("Usando token em cache válido");
            return cachedToken;
        }

        log.info("Obtendo novo token de serviço via Auth V2");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> tokenRequest = Map.of(
                    "clientId", authServiceClientId,
                    "clientSecret", authServiceClientSecret
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(tokenRequest, headers);

            ResponseEntity<ServiceTokenResponse> response = restTemplate.postForEntity(
                    authServiceBaseUrl + authServiceTokenEndpoint,
                    request,
                    ServiceTokenResponse.class
            );

            if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
                cachedToken = response.getBody().accessToken();
                tokenExpiration = Instant.now().plusSeconds(response.getBody().expiresIn()).minusSeconds(60);
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

    record ServiceTokenResponse(String accessToken, Long expiresIn) {
    }
}
