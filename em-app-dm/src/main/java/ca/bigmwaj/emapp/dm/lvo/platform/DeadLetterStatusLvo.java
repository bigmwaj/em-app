package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum DeadLetterStatusLvo {

    RETRY("Retry"),
    SENT("Sent"),
    ERROR("Error");

    @Getter
    private final String description;

    DeadLetterStatusLvo(String description) {
        this.description = description;
    }
}
