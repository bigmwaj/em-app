package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.api.platform.validator.UniqueContactEmail;
import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@UniqueContactEmail
public class ContactEmailDto extends AbstractContactPointDto {

    @NotBlank(message = "Email is required")
    @Size(max = 32, message = "Email must not exceed 32 characters")
    private String email;

    @NotNull(message = "Email type is required")
    private EmailTypeLvo type;

}
