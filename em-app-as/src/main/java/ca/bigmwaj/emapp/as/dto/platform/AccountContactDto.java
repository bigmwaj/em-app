package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountContactDto extends BaseHistDto {

    private Long accountId;

    private ContactDto contact;

    private AccountContactRoleLvo role;

    public boolean isMain() {
        return AccountContactRoleLvo.PRINCIPAL.equals(role);
    }
}
