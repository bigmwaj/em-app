package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractContactPointDto extends AbstractBaseDto {

    private Long id;

    private Long contactId;

    private HolderTypeLvo holderType;

    private Boolean defaultContactPoint;

}
