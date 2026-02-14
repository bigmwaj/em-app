package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum UsernameTypeLvo {

    BASIC("Basic"),
    PHONE("Phone"),
    EMAIL("Email");

    @Getter
    private final String description;

    UsernameTypeLvo(String description) {
        this.description = description;
    }
}
