package ca.bigmwaj.emapp.as.validator.xml;

/**
 * Exception thrown when validation configuration cannot be loaded or parsed.
 */
public class ValidationConfigurationException extends RuntimeException {

    public ValidationConfigurationException(String message) {
        super(message);
    }

    public ValidationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
