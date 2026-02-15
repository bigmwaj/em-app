package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.AddressTypeLvo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactAddressDto extends AbstractContactPointDto {

    private String address;

    private AddressTypeLvo type;

    private String country;

    private String region;

    private String city;

}
