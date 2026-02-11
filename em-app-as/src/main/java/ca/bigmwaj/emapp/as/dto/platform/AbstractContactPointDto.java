package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AddressTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractContactPointDto extends BaseHistDto {

    private Long id;

    private Long contactId;

    private HolderTypeLvo holderType;

    private Boolean defaultContactPoint;


}
