package ca.bigmwaj.emapp.as.dto.security;

import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AuthenticatedUser extends User {

    private static final long serialVersionUID = 2427906991409676359L;
    @Getter
    private final UserDto userInfo;

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

}
