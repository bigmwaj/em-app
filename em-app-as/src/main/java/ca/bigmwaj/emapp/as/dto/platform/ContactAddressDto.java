package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.AddressTypeLvo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactAddressDto extends AbstractContactPointDto {

    @Size(max = 128, message = "Address must not exceed 128 characters")
    private String address;

    @NotNull(message = "Address type is required")
    private AddressTypeLvo type;

    private String country;

    private String region;

    private String city;

}
