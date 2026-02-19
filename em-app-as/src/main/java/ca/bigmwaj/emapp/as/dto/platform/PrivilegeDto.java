package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ValidDto("platform/privilege")
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivilegeDto extends AbstractBaseDto {

    private Short id;

    private String name;

    private String description;
}
