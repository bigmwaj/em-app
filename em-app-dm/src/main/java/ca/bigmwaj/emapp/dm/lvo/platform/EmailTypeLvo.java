package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum EmailTypeLvo {

    PERSONAL("Personal"),
    WORK("Work");

    @Getter
    private final String description;

    EmailTypeLvo(String description) {
        this.description = description;
    }
}
