package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UsernameTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestAccountDtoBuilder extends AccountDto{

    public static TestAccountDtoBuilderBuilder withDefaults() {
        return TestAccountDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withName("Test Account")
                .withDescription("Test Description")
                .withStatus(AccountStatusLvo.ACTIVE)
                .withAdminUsername("test@example.com")
                .withStatusDate(LocalDateTime.now())
                .withAdminUsernameType(UsernameTypeLvo.EMAIL);
    }

    public static TestAccountDtoBuilderBuilder builderWithAllDefaults() {
        return (TestAccountDtoBuilderBuilder) TestAccountDtoBuilder
                .withDefaults()
                .withAccountContact(TestAccountContactDtoBuilder.builderWithAllDefaults().build());
    }
}

