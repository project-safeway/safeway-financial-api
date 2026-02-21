package com.safeway.financial.infrastructure.config;

import com.safeway.financial.infrastructure.security.ServiceAuthenticationProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmartFeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATTION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final ServiceAuthenticationProvider serviceAuthProvider;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String token;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            token = jwt.getTokenValue();
            log.debug("Usando token do usuário logado");
        } else {
            token = serviceAuthProvider.getServiceToken();
            log.debug("Usando token do serviço");
        }

        requestTemplate.header(AUTHORIZATTION_HEADER, BEARER_PREFIX + token);
    }
}
