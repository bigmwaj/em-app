package ca.bigmwaj.emapp.as.lvo.platform;

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
