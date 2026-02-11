package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.api.platform.validator.UniqueContactPhone;
import ca.bigmwaj.emapp.dm.dto.BaseHistDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@UniqueContactPhone
public class ContactPhoneDto extends AbstractContactPointDto {

    @NotBlank(message = "Phone is required")
    @Size(max = 32, message = "Phone must not exceed 32 characters")
    private String phone;

    @NotNull(message = "Phone type is required")
    private PhoneTypeLvo type;

}
