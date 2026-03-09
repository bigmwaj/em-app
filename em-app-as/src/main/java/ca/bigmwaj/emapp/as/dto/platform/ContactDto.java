package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

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
