package com.safeway.financial.presentation.handler;

import java.time.LocalDateTime;

public record ExceptionHandlerResponse(
        String token,
        String message,
        Integer status,
        LocalDateTime timestamp
) {
}
