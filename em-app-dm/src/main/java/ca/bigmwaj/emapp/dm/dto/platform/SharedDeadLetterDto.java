package ca.bigmwaj.emapp.dm.dto.platform;

import ca.bigmwaj.emapp.dm.dto.AbstractStatusTrackingDto;
import ca.bigmwaj.emapp.dm.lvo.platform.DeadLetterStatusLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class SharedDeadLetterDto extends AbstractStatusTrackingDto<DeadLetterStatusLvo> {

    private Long id;

    private String eventName;

    private String message;

    private String errorMessage;

    private DeadLetterStatusLvo status;
}
