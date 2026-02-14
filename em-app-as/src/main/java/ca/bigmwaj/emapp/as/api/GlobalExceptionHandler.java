package ca.bigmwaj.emapp.as.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        logger.error("Une erreur est survenue lors du traitement de votre requette.", ex);
        return ResponseEntity
                .internalServerError()
                .body("Une erreur s'est produite lors du traitement de votre requête. Veuillez contacter le support si le problème persiste.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NoResourceFoundException ex) {
        logger.error("La ressource demandée n'a pas été trouvée!", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<String> handleValidationException(HandlerMethodValidationException ex) {
        logger.error("Erreur de validation", ex);
        var msg = Arrays.stream(ex.getDetailMessageArguments())
                .map(Object::toString)
                .collect(Collectors.joining());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Erreur de validation de votre requête:\n" + msg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Erreur de validation", ex);

        Function<FieldError, String> errorToMessage = error ->
                String.format("[%s]: %s", error.getField(), error.getDefaultMessage());

        var msg = ex.getFieldErrors().stream()
                .map(errorToMessage)
                .collect(Collectors.joining("\n"));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Erreur de validation de votre requête:\n" + msg);
    }
    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentConversionNotSupportedException ex) {
        logger.error("Erreur de conversion du champ {}. Message {}", ex.getName(), ex.getLocalizedMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Erreur de conversion lors du traitement de votre requête.\n" + ex.getMessage());
    }
}
