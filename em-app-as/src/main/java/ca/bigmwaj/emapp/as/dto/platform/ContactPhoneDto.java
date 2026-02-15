package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactPhoneDto extends AbstractContactPointDto {

    private String phone;

    private PhoneTypeLvo type;

}
