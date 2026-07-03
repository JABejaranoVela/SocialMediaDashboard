package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.validation.InvalidCatalogValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidCatalogValueException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCatalogValue(InvalidCatalogValueException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", exception.getMessage()));
    }
}
