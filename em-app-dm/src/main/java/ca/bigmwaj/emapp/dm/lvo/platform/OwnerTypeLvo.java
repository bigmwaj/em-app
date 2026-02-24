package ca.bigmwaj.emapp.dm.lvo.platform;

import lombok.Getter;

public enum OwnerTypeLvo {

    CORPORATE("Corporate"),
    ACCOUNT("Account"),;

    @Getter
    private final String description;

    OwnerTypeLvo(String description) {
        this.description = description;
    }
}
