package ca.bigmwaj.emapp.as.entity.platform;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RolePrivilegePK implements Serializable {

    private Short role;

    private Short privilege;

    public RolePrivilegePK(@Nonnull RolePrivilegeEntity entity) {
        super();
        this.privilege = entity.getPrivilege().getId();
        this.role = entity.getRole().getId();
    }
}
