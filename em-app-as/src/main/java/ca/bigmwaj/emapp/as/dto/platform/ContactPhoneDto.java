package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactPhoneDto extends AbstractContactPointDto {

    private String phone;

    private PhoneTypeLvo type;

}
