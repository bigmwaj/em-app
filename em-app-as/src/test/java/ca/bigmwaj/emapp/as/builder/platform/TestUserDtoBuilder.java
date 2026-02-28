package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.builder.common.TestConstant;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UsernameTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestUserDtoBuilder extends UserDto {

    public static TestUserDtoBuilderBuilder withDefaults() {
        return TestUserDtoBuilder.builder()
                .withCreatedBy(TestConstant.TEST_USER)
                .withUpdatedBy(TestConstant.TEST_USER)
                .withCreatedDate(LocalDateTime.now())
                .withUpdatedDate(LocalDateTime.now())
                .withEditAction(EditActionLvo.CREATE)
                .withUsername(TestConstant.TEST_USER)
                .withPassword(TestConstant.TEST_PASSWORD)
                .withOwnerType(OwnerTypeLvo.CORPORATE)
                .withStatus(UserStatusLvo.ACTIVE)
                .withUsernameType(UsernameTypeLvo.BASIC);
    }

    /**
     * Convenience method to create a UserDto builder pre-populated with all default values.
     * The service method should be able to use this builder to successfully create a valid UserDto with all
     * required fields and a complete ContactDto with default contact points.
     *
     * @return a valid default userDto including a default ContactDto with :
     * <ul>
     * <li> default email, </li>
     * <li> default phone,</li>
     * <li> default address.</li>
     * </ul>
     */
    public static TestUserDtoBuilderBuilder builderWithAllDefaults() {
        return (TestUserDtoBuilderBuilder) TestUserDtoBuilder.withDefaults()
                .withUserRole(TestUserRoleDtoBuilder.withDefaults().build())
                .withContact(TestContactDtoBuilder.builderWithAllDefaults(OwnerTypeLvo.CORPORATE).build());
    }
}
