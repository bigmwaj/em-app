package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractStatusTrackingEntity;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UsernameTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_USER")
@Data
public class UserEntity extends AbstractStatusTrackingEntity<UserStatusLvo> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Short id;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private UserStatusLvo status;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "USERNAME_VERIFIED")
    private Boolean usernameVerified;

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
    @Column(name = "OWNER_TYPE", nullable = false, updatable = false)
    private OwnerTypeLvo ownerType;

    public Object getDefaultKey() {
        return id;
    }
}
