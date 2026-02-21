package com.safeway.financial.infrastructure.security;

import com.safeway.financial.application.ports.input.AuthenticationPort;
import com.safeway.financial.domain.exceptions.OperationNotAlloyedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationAdapter implements AuthenticationPort {

    @Override
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new OperationNotAlloyedException("User is not authenticated");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getSubject();
            return UUID.fromString(userId);
        }

        String name = authentication.getName();
        return UUID.fromString(name);
    }

}
