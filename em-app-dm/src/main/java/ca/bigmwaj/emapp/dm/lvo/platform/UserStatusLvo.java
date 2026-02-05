package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum UserStatusLvo {

    ACTIVE("Active"),
    BLOCKED("Blocked");

    @Getter
    private final String description;

    UserStatusLvo(String description) {
        this.description = description;
    }
}
