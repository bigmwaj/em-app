package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

@ValidDto("platform/role")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class RoleDto extends AbstractChangeTrackingDto {

    private Short id;

    private String name;

    private String description;

    private OwnerTypeLvo ownerType;

    @Singular
    private List<RolePrivilegeDto> rolePrivileges;

    @Singular
    private List<RoleUserDto> roleUsers;
}
