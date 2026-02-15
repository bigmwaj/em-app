package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ValidDto("platform/group-user")
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupUserDto extends BaseHistDto {

    private Short groupId;

    private Short userId;
}
