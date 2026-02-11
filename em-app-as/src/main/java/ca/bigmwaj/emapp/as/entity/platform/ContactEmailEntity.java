package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "PLATFORM_CONTACT_EMAIL",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"EMAIL", "HOLDER_TYPE"},
                        name = "PLATFORM_CONTACT_EMAIL_UK_EMAIL_HOLDER_TYPE"
                )
        }
)
@Data
public class ContactEmailEntity extends AbstractContactPointEntity {

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private EmailTypeLvo type;
}
