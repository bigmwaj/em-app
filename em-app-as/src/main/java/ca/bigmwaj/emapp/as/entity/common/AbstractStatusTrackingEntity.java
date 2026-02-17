package ca.bigmwaj.emapp.as.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
public abstract class AbstractStatusTrackingEntity<T> extends AbstractChangeTrackingEntity {

    public abstract T getStatus();

    public abstract void setStatus(T status);

    @Column(name = "STATUS_REASON")
    private String statusReason;

    @Column(name = "STATUS_DATE")
    private LocalDateTime statusDate;

}
