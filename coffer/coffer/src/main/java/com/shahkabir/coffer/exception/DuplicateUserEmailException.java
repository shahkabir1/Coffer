package com.shahkabir.coffer.exception;

public class DuplicateUserEmailException extends RuntimeException {
    public DuplicateUserEmailException(String message) {
        super(message);
    }
}
