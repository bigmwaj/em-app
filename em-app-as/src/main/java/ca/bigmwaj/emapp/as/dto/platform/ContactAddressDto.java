package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AddressTypeLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactAddressDto extends BaseHistDto {

    private Long id;

    private String address;

    private AddressTypeLvo type;

    private ContactDto contact;

    private boolean toDelete;

    @JsonIgnore
    public boolean isNotToDelete(){
        return !toDelete;
    }

}
