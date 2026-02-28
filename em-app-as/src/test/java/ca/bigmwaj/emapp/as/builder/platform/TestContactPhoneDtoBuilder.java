package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactPhoneDto;
import ca.bigmwaj.emapp.as.lvo.platform.PhoneTypeLvo;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class TestContactPhoneDtoBuilder extends ContactPhoneDto{
    public static TestContactPhoneDtoBuilderBuilder withDefaults(){
        return TestContactPhoneDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withType(PhoneTypeLvo.WORK)
                .withPhone("4182552407")
                .withOwnerType(OwnerTypeLvo.ACCOUNT)
                .withDefaultContactPoint(true)
                .withIndicative("+1");
    }
}
