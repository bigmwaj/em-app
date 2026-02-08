package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@IdClass(AccountContactPK.class)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_ACCOUNT_CONTACT")
@Data
public class AccountContactEntity extends AbstractBaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private AccountEntity account;

    @Id
    @ManyToOne
    @JoinColumn(name = "CONTACT_ID", nullable = false)
    private ContactEntity contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private AccountContactRoleLvo role;
}
