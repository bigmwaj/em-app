package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@ValidDto("platform/group")
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupDto extends AbstractChangeTrackingDto {

    private Short id;

    private String name;

    private String description;

    private HolderTypeLvo holderType;

    private List<GroupRoleDto> groupRoles;

    private List<GroupUserDto> groupUsers;
}
