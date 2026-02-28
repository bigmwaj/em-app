package ca.bigmwaj.emapp.as.lvo.platform;

import lombok.Getter;

public enum AccountContactRoleLvo {

    PRINCIPAL("Principal"),
    AGENT("Agent");

    @Getter
    private final String description;

    AccountContactRoleLvo(String description) {
        this.description = description;
    }
}
