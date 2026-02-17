package ca.bigmwaj.emapp.as.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class AbstractBaseEntity {

    @Transient
    private Object key;

    public abstract Object getDefaultKey();

}
