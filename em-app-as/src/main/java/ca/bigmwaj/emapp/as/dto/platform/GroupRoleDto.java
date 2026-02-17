package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupRoleDto extends AbstractChangeTrackingDto {

    private Short groupId;

    private RoleDto role;
}
