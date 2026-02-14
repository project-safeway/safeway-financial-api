package com.safeway.financial.domain.exceptions;

public class MensalidadeNotFoundException extends RuntimeException {
    public MensalidadeNotFoundException(String message) {
        super(message);
    }
}
