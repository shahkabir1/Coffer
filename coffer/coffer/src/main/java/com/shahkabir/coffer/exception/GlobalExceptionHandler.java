package com.shahkabir.coffer.exception;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CustomerNotFoundException.class,
            AccountNotFoundException.class,
            TransactionNotFoundException.class,
            EntryNotFoundException.class
    })

    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        ApiError error = new ApiError(
                "NOT_FOUND",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    public record ApiError(String code, String message){
    }

    @ExceptionHandler({DuplicateCustomerEmailException.class})
    public ResponseEntity<ApiError> handleDuplicateEmail(
            DuplicateCustomerEmailException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError("EMAIL_ALREADY_EXISTS",
                        ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        return ResponseEntity
                .badRequest()
                .body(new ApiError("VALIDATION_ERROR", message));
    }
}
