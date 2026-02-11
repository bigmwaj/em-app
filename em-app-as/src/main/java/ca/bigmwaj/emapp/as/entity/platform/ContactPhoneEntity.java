package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "PLATFORM_CONTACT_PHONE",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"PHONE", "HOLDER_TYPE"},
                        name = "PLATFORM_CONTACT_PHONE_UK_PHONE_HOLDER_TYPE"
                )
        }
)
@Data
public class ContactPhoneEntity extends AbstractContactPointEntity {

    @Column(name = "PHONE", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private PhoneTypeLvo type;

}
