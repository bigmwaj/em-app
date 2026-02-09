package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDto extends BaseHistDto {

    private Long id;

    private String name;

    private String description;

    private AccountStatusLvo status;

    private List<AccountContactDto> accountContacts;

    private ContactDto mainContact;
}