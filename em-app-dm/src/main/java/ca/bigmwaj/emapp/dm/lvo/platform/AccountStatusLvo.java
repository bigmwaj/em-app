package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum AccountStatusLvo {

    ACTIVE("Active"),
    BLOCKED("Blocked");

    @Getter
    private final String description;

    AccountStatusLvo(String description) {
        this.description = description;
    }
}
