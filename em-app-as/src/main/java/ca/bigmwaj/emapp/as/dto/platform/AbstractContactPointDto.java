package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class AbstractContactPointDto extends AbstractBaseDto {

    private Long id;

    private Long contactId;

    private HolderTypeLvo holderType;

    private Boolean defaultContactPoint;

}
