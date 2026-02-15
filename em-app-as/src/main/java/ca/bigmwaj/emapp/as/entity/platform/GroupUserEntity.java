package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@IdClass(GroupUserPK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_GROUP_USER")
@Data
public class GroupUserEntity extends AbstractBaseEntity {

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
