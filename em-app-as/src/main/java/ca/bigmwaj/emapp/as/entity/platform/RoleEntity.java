package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_ROLE")
@Data
public class RoleEntity extends AbstractChangeTrackingEntity {

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
    @Column(name = "OWNER_TYPE", nullable = false, updatable = false)
    private OwnerTypeLvo ownerType;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePrivilegeEntity> rolePrivileges = new ArrayList<>();

    public Object getDefaultKey() {
        return id;
    }
}
