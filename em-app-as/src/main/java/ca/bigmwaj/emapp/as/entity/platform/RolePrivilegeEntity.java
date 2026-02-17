package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@IdClass(RolePrivilegePK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_ROLE_PRIVILEGE")
@Data
public class RolePrivilegeEntity extends AbstractChangeTrackingEntity {

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
