package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRoleDto extends BaseHistDto {

    private Short userId;

    private RoleDto role;
}
