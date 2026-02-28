package ca.bigmwaj.emapp.as.lvo.platform;

import lombok.Getter;

public enum AddressTypeLvo {

    HOME("Home"),
    WORK("Work");

    @Getter
    private final String description;

    AddressTypeLvo(String description) {
        this.description = description;
    }
}
