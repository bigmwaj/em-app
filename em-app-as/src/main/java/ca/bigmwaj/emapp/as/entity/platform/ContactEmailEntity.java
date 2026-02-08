package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_CONTACT_EMAIL")
@Data
public class ContactEmailEntity extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Long id;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private EmailTypeLvo type;

    @ManyToOne
    @JoinColumn(name = "CONTACT_ID", nullable = false)
    private ContactEntity contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "HOLDER_TYPE", nullable = false)
    private HolderTypeLvo holderType;
}
