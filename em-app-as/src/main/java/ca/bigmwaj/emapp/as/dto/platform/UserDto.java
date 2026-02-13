package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends BaseHistDto {

    private Long id;

    private String picture;

    private String provider;

    @NotNull(message = "Username type is required")
    private String username;

    private String password;

    @NotNull(message = "Contact type is required")
    @Valid
    private ContactDto contact;

    private UserStatusLvo status;

    private LocalDateTime statusDate;

    private String statusReason;

    @NotNull(message = "Holder type is required")
    private HolderTypeLvo holderType;
}
