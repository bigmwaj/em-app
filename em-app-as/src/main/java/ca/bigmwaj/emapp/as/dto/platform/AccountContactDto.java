package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountContactDto extends BaseHistDto {

    private Short accountId;

    @NotNull(message = "Contact is required")
    @Valid
    private ContactDto contact;

    @NotNull(message = "Contact role is required")
    private AccountContactRoleLvo role;

    public boolean isMain() {
        return AccountContactRoleLvo.PRINCIPAL.equals(role);
    }
}
