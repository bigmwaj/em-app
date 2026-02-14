package ca.bigmwaj.emapp.as.validator;

/**
 * Exception thrown when validating dto.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
