package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactPhoneDto extends BaseHistDto {

    private Long id;

    private String phone;

    private PhoneTypeLvo type;

    private ContactDto contact;

    private boolean toDelete;

    @JsonIgnore
    public boolean isNotToDelete(){
        return !toDelete;
    }

}
