package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.AbstractStatusTrackingDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

@ValidDto("platform/account")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class AccountDto extends AbstractStatusTrackingDto<AccountStatusLvo> {

    private Short id;

    private String name;

    private String description;

    private AccountStatusLvo status;

    @Singular
    private List<AccountContactDto> accountContacts;

    private String adminUsername;

    private UsernameTypeLvo adminUsernameType;

    private String adminUsernameTypePhoneIndicative;

    public boolean isAdminUsernameTypePhone() {
        return UsernameTypeLvo.PHONE.equals(adminUsernameType);
    }

    public boolean isAdminUsernameTypeEmail() {
        return UsernameTypeLvo.EMAIL.equals(adminUsernameType);
    }
}