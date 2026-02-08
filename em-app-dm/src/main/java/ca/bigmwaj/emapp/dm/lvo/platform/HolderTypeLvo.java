package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum HolderTypeLvo {

    CORPORATE("Corporate"),
    ACCOUNT("Account"),;

    @Getter
    private final String description;

    HolderTypeLvo(String description) {
        this.description = description;
    }
}
