package ca.bigmwaj.emapp.dm.lvo.platform;

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
