package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractStatusTrackingEntity;
import ca.bigmwaj.emapp.as.lvo.platform.AccountStatusLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_ACCOUNT")
@Data
public class AccountEntity extends AbstractStatusTrackingEntity<AccountStatusLvo> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Short id;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccountStatusLvo status;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountContactEntity> accountContacts = new ArrayList<>();

    public Object getDefaultKey() {
        return id;
    }
}
