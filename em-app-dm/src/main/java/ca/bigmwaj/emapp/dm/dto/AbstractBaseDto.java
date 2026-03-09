package ca.bigmwaj.emapp.dm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public abstract class AbstractBaseDto {

    private boolean New;

    private boolean retired;

    private Object key;

    @Deprecated
    @JsonIgnore
    public boolean isNotToDelete() {
        return !retired;
    }

    @Deprecated
    @JsonIgnore
    public boolean isToDelete() {
        return retired;
    }

    public boolean isCreateAction() {
        return New;
    }

    public boolean isDeleteAction() {
        return retired;
    }

    public boolean isUpdateAction() {
        return !New && !retired;
    }
}
