package ca.bigmwaj.emapp.as.dto.security;

import java.util.Collection;

import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

public class AuthenticatedUser extends User{

    private static final long serialVersionUID = 2427906991409676359L;

    public AuthenticatedUser(UserDto userInfo, boolean enabled, boolean accountNonExpired,
                             boolean credentialsNonExpired, boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {

        super(userInfo.getUsername(), userInfo.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userInfo = userInfo;
    }

    public AuthenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userInfo = null;
    }

    @Getter
    private final UserDto userInfo;

}
