package es.studium.dashboard.app.validation;

public class InvalidCatalogValueException extends RuntimeException {
    public InvalidCatalogValueException(String message) {
        super(message);
    }
}
