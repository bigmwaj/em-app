package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@IdClass(GroupUserPK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_GROUP_USER")
@Data
public class GroupUserEntity extends AbstractChangeTrackingEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "GROUP_USER_FK_GROUP"))
    private GroupEntity group;

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "GROUP_USER_FK_USER"))
    private UserEntity user;

    public Object getDefaultKey() {
        return new GroupUserPK(this);
    }
}
