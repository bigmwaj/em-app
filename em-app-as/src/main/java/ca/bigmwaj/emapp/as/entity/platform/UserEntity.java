package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_USER")
@Data
public class UserEntity extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Short id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "USERNAME_TYPE", nullable = false)
    private UsernameTypeLvo usernameType;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PROVIDER")
    private String provider;

    @ManyToOne
    @JoinColumn(name = "CONTACT_ID", nullable = false, updatable = false)
    private ContactEntity contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private UserStatusLvo status;

    @Column(name = "STATUS_DATE")
    private LocalDateTime statusDate;

    @Column(name = "STATUS_REASON")
    private String statusReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "HOLDER_TYPE", nullable = false, updatable = false)
    private HolderTypeLvo holderType;

    public Object getDefaultKey() {
        return id;
    }
}
