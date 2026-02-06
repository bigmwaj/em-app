package ca.bigmwaj.emapp.as.dto.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class AuthenticatedUserGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -497870597905063392L;

    private final String authority;
}