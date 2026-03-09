package ca.bigmwaj.emapp.as.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractBaseEntity {

    @Transient
    private Object key;

    @Column(name = "RETIRED")
    private Boolean retired;

    public abstract Object getDefaultKey();

}
