package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.CustomPayload;
import ca.bigmwaj.emapp.as.validator.shared.ValidNotNullOnCreate;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends BaseHistDto {

    private Long id;

    private String picture;

    private String provider;

    @NotNull(message = "Username type is required")
    private String username;

    @ValidNotNullOnCreate(payload = CustomPayload.class)
    private String password;

    @NotNull(message = "Contact type is required")
    @Valid
    private ContactDto contact;

    private UserStatusLvo status;

    @NotNull(message = "Holder type is required")
    private HolderTypeLvo holderType;
}
