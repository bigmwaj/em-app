package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.PrivilegeDto;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestPrivilegeDtoBuilder extends PrivilegeDto {

    public static TestPrivilegeDtoBuilder.TestPrivilegeDtoBuilderBuilder withDefaults() {
        return TestPrivilegeDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withName("DEFAULT_TEST_PRIVILEGE")
                .withDescription("Default test privilege");
    }

    /**
     * This privilege is supposed to always exist in DB and should not be deleted in commited transaction.
     */
    public static TestPrivilegeDtoBuilder.TestPrivilegeDtoBuilderBuilder PERMANENT_PRIVILEGE() {
        return TestPrivilegeDtoBuilder.builder()
                .withEditAction(EditActionLvo.NONE)
                .withName("TEST_PERMANENT_PRIVILEGE")
                .withId((short)1)
                .withDescription("Default test privilege");
    }
}
