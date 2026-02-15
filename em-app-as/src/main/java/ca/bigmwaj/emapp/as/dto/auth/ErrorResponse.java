package ca.bigmwaj.emapp.as.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for error responses from authentication endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * Error message
     */
    private String message;
    
    /**
     * Timestamp of the error
     */
    private long timestamp;
    
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
