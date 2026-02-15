package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePrivilegeDto extends BaseHistDto {

    private Short roleId;

    private PrivilegeDto privilege;
}
