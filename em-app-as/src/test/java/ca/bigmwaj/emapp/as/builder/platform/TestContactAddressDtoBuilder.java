package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactAddressDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AddressTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestContactAddressDtoBuilder extends ContactAddressDto{

    public static TestContactAddressDtoBuilderBuilder withDefaults(){
        return TestContactAddressDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withType(AddressTypeLvo.WORK)
                .withAddress("Test Address")
                .withOwnerType(OwnerTypeLvo.ACCOUNT);
    }
}
