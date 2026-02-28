package ca.bigmwaj.emapp.as.lvo.platform;

import lombok.Getter;

public enum PhoneTypeLvo {

    MOBILE("Mobile"),
    HOME("Home"),
    WORK("Work");

    @Getter
    private final String description;

    PhoneTypeLvo(String description) {
        this.description = description;
    }
}
