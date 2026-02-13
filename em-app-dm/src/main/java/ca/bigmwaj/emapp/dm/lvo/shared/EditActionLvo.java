package ca.bigmwaj.emapp.dm.lvo.shared;

import lombok.Getter;

public enum EditActionLvo {

    NONE("None"),
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    CHANGE_STATUS("Change Status");

    @Getter
    private final String description;

    EditActionLvo(String description) {
        this.description = description;
    }
}
