package com.reliaquest.api.exception;

import com.reliaquest.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles various exceptions and returns appropriate HTTP responses.
 */
@ControllerAdvice(basePackages = "com.reliaquest.api")
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle EmployeeNotFoundException and return a 404 Not Found response.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                "Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle InvalidRequestException and return a 400 response.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidRequestException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                "Bad Request",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MethodArgumentNotValidException and return a 400 Bad Request response.
     *
     * @param ex      the exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handle ConstraintViolationException and return a 400 Bad Request response.
     *
     * @param ex the exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolations(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handle generic exceptions and return a 500 Internal Server Error response.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                "Internal Server Error",
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );

        log.error("Something went wrong: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

