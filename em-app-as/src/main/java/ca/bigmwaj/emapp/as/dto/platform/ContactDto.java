package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.platform.ValidBirthDate;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactDto extends BaseHistDto {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 32, message = "First name must not exceed 32 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 32, message = "Last name must not exceed 32 characters")
    private String lastName;

    @ValidBirthDate
    private LocalDate birthDate;

    @NotBlank(message = "Holder type is required")
    private HolderTypeLvo holderType;

    @NotEmpty(message = "At least one email is required")
    @Valid
    private List<ContactEmailDto> emails;

    @NotEmpty(message = "At least one phone is required")
    @Valid
    private List<ContactPhoneDto> phones;

    @Valid
    private List<ContactAddressDto> addresses;
}
