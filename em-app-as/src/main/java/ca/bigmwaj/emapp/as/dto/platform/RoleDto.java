package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ValidDto("platform/role")
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleDto extends BaseHistDto {

    private Short id;

    private String name;

    private String description;
}
