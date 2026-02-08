package ca.bigmwaj.emapp.as.dto.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(setterPrefix = "with")
public class DefaultFilterDto extends AbstractFilterDto {

}
