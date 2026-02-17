package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ValidDto("platform/privilege")
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivilegeDto extends AbstractChangeTrackingDto {

    private Short id;

    private String name;

    private String description;
}
