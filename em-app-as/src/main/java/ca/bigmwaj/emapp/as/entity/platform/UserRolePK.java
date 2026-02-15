package ca.bigmwaj.emapp.as.entity.platform;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class UserRolePK implements Serializable {

    private Short user;

    private Short role;

    public UserRolePK(@Nonnull UserRoleEntity entity) {
        super();
        this.user = entity.getUser().getId();
        this.role = entity.getRole().getId();
    }
}
