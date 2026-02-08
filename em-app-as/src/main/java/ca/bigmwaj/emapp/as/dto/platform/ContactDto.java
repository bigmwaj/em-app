package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactDto extends BaseHistDto {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private HolderTypeLvo holderType;

    private ContactEmailDto mainEmail;

    private List<ContactEmailDto> emails;

    private ContactPhoneDto mainPhone;

    private List<ContactPhoneDto> phones;

    private ContactAddressDto mainAddress;

    private List<ContactAddressDto> addresses;
}
