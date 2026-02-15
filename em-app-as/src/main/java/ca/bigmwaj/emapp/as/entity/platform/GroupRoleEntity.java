package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@IdClass(GroupRolePK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_GROUP_ROLE")
@Data
public class GroupRoleEntity extends AbstractBaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "GROUP_ROLE_FK_GROUP"))
    private GroupEntity group;

    @Id
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "GROUP_ROLE_FK_ROLE"))
    private RoleEntity role;

    public Object getDefaultKey() {
        return new GroupRolePK(this);
    }
}
