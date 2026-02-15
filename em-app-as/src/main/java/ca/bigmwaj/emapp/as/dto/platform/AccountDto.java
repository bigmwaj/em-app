package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@ValidDto("platform/account")
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDto extends BaseHistDto {

    private Short id;

    private String name;

    private String description;

    private AccountStatusLvo status;

    private LocalDateTime statusDate;

    private String statusReason;

    private List<AccountContactDto> accountContacts;

    private String adminUsername;

    private UsernameTypeLvo adminUsernameType;
}