package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@ValidDto("platform/contact")
@EqualsAndHashCode(callSuper = true)
@Data
public class ContactDto extends BaseHistDto {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private HolderTypeLvo holderType;

    private List<ContactEmailDto> emails;

    private List<ContactPhoneDto> phones;

    private List<ContactAddressDto> addresses;
}
