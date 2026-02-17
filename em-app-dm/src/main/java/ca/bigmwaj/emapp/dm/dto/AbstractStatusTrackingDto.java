package ca.bigmwaj.emapp.dm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractStatusTrackingDto<T> extends AbstractChangeTrackingDto {

    public abstract T getStatus();

    public abstract void setStatus(T status);

    private LocalDateTime statusDate;

    private String statusReason;

}
