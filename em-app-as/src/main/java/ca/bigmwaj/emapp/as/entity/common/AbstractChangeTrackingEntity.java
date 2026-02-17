package ca.bigmwaj.emapp.as.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
public abstract class AbstractChangeTrackingEntity extends AbstractBaseEntity{

    @Column(name = "CREATED_BY", updatable = false, nullable = false)
    private String createdBy;

    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_BY", updatable = false)
    private String updatedBy;

    @Column(name = "UPDATED_DATE", updatable = false)
    private LocalDateTime updatedDate;

}
