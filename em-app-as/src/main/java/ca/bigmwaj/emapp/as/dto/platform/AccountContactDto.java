package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class AccountContactDto extends AbstractChangeTrackingDto {

    private Short accountId;

    private ContactDto contact;

    private AccountContactRoleLvo role;

}
