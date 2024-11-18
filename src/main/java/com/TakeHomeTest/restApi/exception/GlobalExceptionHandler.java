package com.TakeHomeTest.restApi.exception;

import com.TakeHomeTest.restApi.dto.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {





    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GeneralResponse<>(102, errorMessage, null));
    }


	@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GeneralResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GeneralResponse<>(102, ex.getMessage(), null));
    }
	
	@ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<GeneralResponse<Void>> handleJwtException(io.jsonwebtoken.JwtException ex) {
        return ResponseEntity.status(401)
                .body(new GeneralResponse<>(108, "Token tidak valid atau kadaluwarsa", null));
    }
}
