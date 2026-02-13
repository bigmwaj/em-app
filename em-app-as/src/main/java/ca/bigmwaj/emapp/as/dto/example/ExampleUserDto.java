package ca.bigmwaj.emapp.as.dto.example;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.Data;

/**
 * Example DTO demonstrating XML-driven validation.
 * This DTO uses the @ValidDto annotation with namespace "example/user"
 * which maps to the XML file: validator/example.xml
 */
@ValidDto("example/user")
@Data
public class ExampleUserDto {
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private EditActionLvo editAction;
}
