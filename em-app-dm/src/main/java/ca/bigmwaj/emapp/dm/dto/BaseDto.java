package ca.bigmwaj.emapp.dm.dto;

import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public class BaseDto {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean _new;

    private EditActionLvo editAction;

    public boolean isNew() {
        return _new;
    }

    public void setNew(boolean _new) {
        this._new = _new;
    }

    @JsonIgnore
    public boolean isNotToDelete(){
        return !EditActionLvo.DELETE.equals(getEditAction());
    }

    @JsonIgnore
    public boolean isToDelete(){
        return EditActionLvo.DELETE.equals(getEditAction());
    }
}
