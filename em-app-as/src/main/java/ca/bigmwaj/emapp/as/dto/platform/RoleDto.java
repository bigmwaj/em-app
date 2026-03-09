package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
