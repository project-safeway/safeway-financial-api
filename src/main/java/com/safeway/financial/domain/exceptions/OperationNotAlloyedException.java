package com.safeway.financial.domain.exceptions;

public class OperationNotAlloyedException extends RuntimeException {
    public OperationNotAlloyedException(String message) {
        super(message);
    }
}
