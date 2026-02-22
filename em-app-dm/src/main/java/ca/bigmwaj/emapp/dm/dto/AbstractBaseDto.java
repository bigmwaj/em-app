package ca.bigmwaj.emapp.dm.dto;

import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public abstract class AbstractBaseDto {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean _new;

    private Object key;

    private EditActionLvo editAction;

    @JsonIgnore
    public boolean isCreateOrUpdateAction() {
        return isCreateAction() || isUpdateAction();
    }

    @JsonIgnore
    public boolean isCreateAction() {
        return EditActionLvo.CREATE.equals(getEditAction());
    }

    @JsonIgnore
    public boolean isUpdateAction() {
        return EditActionLvo.UPDATE.equals(getEditAction());
    }

    @JsonIgnore
    public boolean isDeleteAction() {
        return EditActionLvo.DELETE.equals(getEditAction());
    }

    @JsonIgnore
    public boolean isUpdateOrChangeStatusAction() {
        return isUpdateAction() || isChangeStatusAction();
    }

    @JsonIgnore
    public boolean isChangeStatusAction() {
        return EditActionLvo.CHANGE_STATUS.equals(getEditAction());
    }

    @JsonIgnore
    public boolean isNew() {
        return _new;
    }

    @JsonIgnore
    public void setNew(boolean _new) {
        this._new = _new;
    }

    @JsonIgnore
    public boolean isNotToDelete() {
        return !EditActionLvo.DELETE.equals(getEditAction());
    }

    @JsonIgnore
    public boolean isToDelete() {
        return EditActionLvo.DELETE.equals(getEditAction());
    }
}
