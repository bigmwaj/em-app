package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import ca.bigmwaj.emapp.dm.dto.platform.SharedDeadLetterDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ValidDto("platform/dead-letter")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class DeadLetterDto extends SharedDeadLetterDto {
}
