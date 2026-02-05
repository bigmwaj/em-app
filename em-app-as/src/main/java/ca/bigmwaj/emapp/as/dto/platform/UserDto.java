package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends BaseHistDto {

    private Long id;

    private String username;

    // TODO: SECURITY - Passwords should be hashed and never exposed in DTOs
    // Consider using separate DTOs for password changes
    private String password;

    private ContactDto contact;

    private UserStatusLvo status;
}
