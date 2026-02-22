package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestAccountContactDtoBuilder extends AccountContactDto{

    public static TestAccountContactDtoBuilderBuilder withDefaults() {
        return TestAccountContactDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withAccountId((short) 1)
                .withRole(AccountContactRoleLvo.PRINCIPAL);
    }


    public static TestAccountContactDtoBuilderBuilder builderWithAllDefaults() {
        return (TestAccountContactDtoBuilderBuilder) TestAccountContactDtoBuilder
                .withDefaults()
                .withContact(TestContactDtoBuilder.builderWithAllDefaults().build());
    }
}
