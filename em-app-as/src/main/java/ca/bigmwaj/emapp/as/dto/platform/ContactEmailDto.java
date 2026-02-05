package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactEmailDto extends BaseHistDto {

    private Long id;

    private String email;

    private EmailTypeLvo type;

    private ContactDto contact;

    private boolean toDelete;

    @JsonIgnore
    public boolean isNotToDelete(){
        return !toDelete;
    }
}
