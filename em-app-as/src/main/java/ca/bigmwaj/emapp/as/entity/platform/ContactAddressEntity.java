package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.lvo.platform.AddressTypeLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_CONTACT_ADDRESS")
@Data
public class ContactAddressEntity extends AbstractContactPointEntity {

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private AddressTypeLvo type;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "REGION")
    private String region;

    @Column(name = "CITY")
    private String city;

}
