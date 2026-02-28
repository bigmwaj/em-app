package ca.bigmwaj.emapp.as.lvo.platform;

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
