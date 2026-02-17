package ca.bigmwaj.emapp.dm.dto;

import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class AbstractBaseDto {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean _new;

    private Object key;

    private EditActionLvo editAction;

    public boolean isCreateOrUpdateAction() {
        return isCreateAction() || isUpdateAction();
    }

    public boolean isCreateAction() {
        return EditActionLvo.CREATE.equals(getEditAction());
    }

    public boolean isUpdateAction() {
        return EditActionLvo.UPDATE.equals(getEditAction());
    }

    public boolean isUpdateOrChangeStatusAction() {
        return isUpdateAction() || isChangeStatusAction();
    }

    public boolean isChangeStatusAction() {
        return EditActionLvo.CHANGE_STATUS.equals(getEditAction());
    }

    public boolean isNew() {
        return _new;
    }

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
