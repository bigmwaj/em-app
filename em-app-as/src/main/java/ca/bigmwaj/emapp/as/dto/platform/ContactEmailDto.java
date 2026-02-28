package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.lvo.platform.EmailTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class ContactEmailDto extends AbstractContactPointDto {

    private String email;

    private EmailTypeLvo type;

}
