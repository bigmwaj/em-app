package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@IdClass(RolePrivilegePK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_ROLE_PRIVILEGE")
@Data
public class RolePrivilegeEntity extends AbstractBaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "ROLE_PRIVILEGE_FK_ROLE"))
    private RoleEntity role;

    @Id
    @ManyToOne
    @JoinColumn(name = "PRIVILEGE_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "ROLE_PRIVILEGE_FK_PRIVILEGE"))
    private PrivilegeEntity privilege;

    public Object getDefaultKey() {
        return new RolePrivilegePK(this);
    }
}
