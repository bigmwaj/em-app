package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactEmailDto extends AbstractContactPointDto {

    private String email;

    private EmailTypeLvo type;

}
