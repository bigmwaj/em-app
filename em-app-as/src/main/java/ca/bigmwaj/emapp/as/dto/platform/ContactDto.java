package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@ValidDto("platform/contact")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class ContactDto extends AbstractChangeTrackingDto {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private OwnerTypeLvo ownerType;

    @Singular
    private List<ContactEmailDto> emails;

    @Singular
    private List<ContactPhoneDto> phones;

    @Singular
    private List<ContactAddressDto> addresses;
}
