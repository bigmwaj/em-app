package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends BaseHistDto {

    private Long id;

    private String picture;

    private String provider;

    private String username;

    // SECURITY WARNING: Passwords should never be exposed in DTOs
    // TODO: Create separate DTOs for password changes (e.g., ChangePasswordDto)
    //       Remove this field from read operations
    //       Use @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) at minimum
    private String password;

    private ContactDto contact;

    private UserStatusLvo status;

    private HolderTypeLvo holderType;
}
