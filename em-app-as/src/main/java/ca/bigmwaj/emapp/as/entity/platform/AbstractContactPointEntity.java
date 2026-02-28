package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass()
@Data
public abstract class AbstractContactPointEntity extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CONTACT_ID", nullable = false, updatable = false)
    private ContactEntity contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "OWNER_TYPE", nullable = false, updatable = false)
    private OwnerTypeLvo ownerType;

    @Column(name = "DEFAULT_CONTACT_POINT")
    private Boolean defaultContactPoint;

    public Object getDefaultKey() {
        return id;
    }

}
