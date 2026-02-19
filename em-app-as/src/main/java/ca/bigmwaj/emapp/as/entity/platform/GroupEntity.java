package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_GROUP")
@Data
public class GroupEntity extends AbstractChangeTrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Short id;

    @Column(name = "NAME", nullable = false, unique = true, updatable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "HOLDER_TYPE", nullable = false, updatable = false)
    private HolderTypeLvo holderType;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupRoleEntity> groupRoles = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupUserEntity> groupUsers = new ArrayList<>();


    public Object getDefaultKey() {
        return id;
    }

}
