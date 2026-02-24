package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.builder.common.TestConstant;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestRoleDtoBuilder extends RoleDto {

    public static TestRoleDtoBuilder.TestRoleDtoBuilderBuilder withDefaults() {
        return TestRoleDtoBuilder.builder()
                .withCreatedBy(TestConstant.TEST_USER)
                .withUpdatedBy(TestConstant.TEST_USER)
                .withCreatedDate(LocalDateTime.now())
                .withUpdatedDate(LocalDateTime.now())
                .withEditAction(EditActionLvo.CREATE)
                .withName(TestConstant.TEST_ROLE)
                .withHolderType(OwnerTypeLvo.CORPORATE);
    }

    /**
     * Convenience method to create a RoleDto builder pre-populated with all default values.
     * The service method should be able to use this builder to successfully create a valid RoleDto with all
     * required fields and complete lists of RoleUserDto and RolePrivilegeDto with default values.
     *
     * @return a valid default RoleDto including :
     * <ul>
     * <li> a list of one default RoleUserDto existing in the DB</li>
     * <li> a list of one default RolePrivilegeDto existing in the DB.</li>
     * </ul>
     */
    public static TestRoleDtoBuilder.TestRoleDtoBuilderBuilder builderWithAllDefaults() {
        return (TestRoleDtoBuilder.TestRoleDtoBuilderBuilder) TestRoleDtoBuilder.withDefaults()
                .withRoleUser(TestRoleUserDtoBuilder.withDefaults().build())
                .withRolePrivilege(TestRolePrivilegeDtoBuilder.withDefaults().build());
    }
}
