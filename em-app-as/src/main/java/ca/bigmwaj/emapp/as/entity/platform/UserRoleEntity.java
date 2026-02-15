package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@IdClass(UserRolePK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_USER_ROLE")
@Data
public class UserRoleEntity extends AbstractBaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "USER_ROLE_FK_USER"))
    private UserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "USER_ROLE_FK_ROLE"))
    private RoleEntity role;

    public Object getDefaultKey() {
        return new UserRolePK(this);
    }
}
