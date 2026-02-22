package ca.bigmwaj.emapp.dm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public abstract class AbstractStatusTrackingDto<T> extends AbstractChangeTrackingDto {

    public abstract T getStatus();

    public abstract void setStatus(T status);

    private LocalDateTime statusDate;

    private String statusReason;

}
