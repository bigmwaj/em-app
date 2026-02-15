package ca.bigmwaj.emapp.as.lvo.platform;

import lombok.Getter;

public enum PrivilegeLvo {

    PLATFORM_ACCOUNT_VIEW("Platform Account - View"),
    PLATFORM_ACCOUNT_CREATE("Platform Account - Create"),
    PLATFORM_ACCOUNT_UPDATE("Platform Account - Update"),
    PLATFORM_ACCOUNT_DELETE("Platform Account - Delete"),
    PLATFORM_ACCOUNT_CHANGE_STATUS_ACTIVE("Platform Account - Change Status to Active"),
    PLATFORM_ACCOUNT_CHANGE_STATUS_BLOCKED("Platform Account - Change Status to Blocked"),
    PLATFORM_USER_VIEW("Platform User - View"),
    PLATFORM_USER_CREATE("Platform User - Create"),
    PLATFORM_USER_UPDATE("Platform User - Update"),
    PLATFORM_USER_DELETE("Platform User - Delete"),
    PLATFORM_USER_CHANGE_STATUS_ACTIVE("Platform User - Change Status to Active"),
    PLATFORM_USER_CHANGE_STATUS_BLOCKED("Platform User - Change Status to Blocked"),

    ;

    @Getter
    private final String description;

    PrivilegeLvo(String description) {
        this.description = description;
    }
}
