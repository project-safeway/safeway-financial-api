package com.safeway.financial.presentation.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ExceptionHandlerResponse> handleGlobalExceptions(Exception ex, HttpServletRequest request) {

        String correlationId = MDC.get("X-Correlation-ID");

        ExceptionHandlerResponse response = new ExceptionHandlerResponse(
                correlationId,
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
