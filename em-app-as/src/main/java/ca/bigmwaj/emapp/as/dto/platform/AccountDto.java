package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.platform.UniqueUsername;
import ca.bigmwaj.emapp.as.validator.platform.ValidAccountContacts;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDto extends BaseHistDto {

    private Long id;

    @NotBlank(message = "Account name is required")
    @Size(max = 32, message = "Account name must not exceed 32 characters")
    private String name;

    @Size(max = 256, message = "Description must not exceed 256 characters")
    private String description;

    private AccountStatusLvo status;

    @NotEmpty(message = "At least one account contact is required")
    @ValidAccountContacts
    @Valid
    private List<AccountContactDto> accountContacts;

    /*
     * Use to pass the account username when creating account,
     * and will be used to create the main user for the account.
     */
    @NotBlank(message = "Account username is required")
    @Size(max = 16, message = "Account username must not exceed 16 characters")
    @UniqueUsername
    private String accountUsername;
}