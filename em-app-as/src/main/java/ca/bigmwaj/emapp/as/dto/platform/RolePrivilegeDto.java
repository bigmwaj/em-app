package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePrivilegeDto extends AbstractChangeTrackingDto {

    private Short roleId;

    private PrivilegeDto privilege;
}
