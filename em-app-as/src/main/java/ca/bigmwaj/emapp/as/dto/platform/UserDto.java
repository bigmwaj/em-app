package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@ValidDto("platform/user")
public class UserDto extends BaseHistDto {

    private Short id;

    private String picture;

    private String provider;

    private String username;

    private Boolean usernameVerified;

    private UsernameTypeLvo usernameType;

    /**
     * Password field is write-only to prevent exposure in API responses.
     * Used only for creating/updating users with password authentication.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private ContactDto contact;

    private UserStatusLvo status;

    private LocalDateTime statusDate;

    private String statusReason;

    private HolderTypeLvo holderType;
}
