package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactEmailDto;
import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
public class TestContactEmailDtoBuilder extends ContactEmailDto{

    public static TestContactEmailDtoBuilderBuilder withDefaults(){
        return TestContactEmailDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withType(EmailTypeLvo.WORK)
                .withEmail("test@example.com")
                .withHolderType(OwnerTypeLvo.ACCOUNT);
    }
}
