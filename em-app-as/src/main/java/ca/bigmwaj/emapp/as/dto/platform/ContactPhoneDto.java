package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class ContactPhoneDto extends AbstractContactPointDto {

    private String phone;

    private PhoneTypeLvo type;

    private String indicative;

    private String extension;

}
