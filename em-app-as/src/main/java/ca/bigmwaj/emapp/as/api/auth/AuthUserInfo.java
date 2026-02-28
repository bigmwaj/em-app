package ca.bigmwaj.emapp.as.api.auth;

import ca.bigmwaj.emapp.as.lvo.platform.UsernameTypeLvo;
import lombok.Data;

@Data
public class AuthUserInfo {
    private String email;
    private String phone;
    private UsernameTypeLvo usernameType;
    private boolean validatedUsername;
    private String name;
    private String picture;
}
