package ca.bigmwaj.emapp.as.api.auth;

import lombok.Data;

@Data
public class UserInfo {
    private String email;
    private String name;
    private String picture;
}
