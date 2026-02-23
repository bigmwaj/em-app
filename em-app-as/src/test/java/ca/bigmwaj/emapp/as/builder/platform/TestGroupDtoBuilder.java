package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.builder.common.TestConstant;
import ca.bigmwaj.emapp.as.dto.platform.GroupDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestGroupDtoBuilder extends GroupDto {

    public static TestGroupDtoBuilder.TestGroupDtoBuilderBuilder withDefaults() {
        return TestGroupDtoBuilder.builder()
                .withCreatedBy(TestConstant.TEST_USER)
                .withUpdatedBy(TestConstant.TEST_USER)
                .withCreatedDate(LocalDateTime.now())
                .withUpdatedDate(LocalDateTime.now())
                .withEditAction(EditActionLvo.CREATE)
                .withName(TestConstant.TEST_GROUP)
                .withHolderType(HolderTypeLvo.CORPORATE);
    }

    /**
     * Convenience method to create a RoleDto builder pre-populated with all default values.
     * The service method should be able to use this builder to successfully create a valid RoleDto with all
     * required fields and complete lists of GroupUserDto and GroupRoleDto with default values.
     *
     * @return a valid default GroupDto including :
     * <ul>
     * <li> a list of one default GroupUserDto existing in the DB</li>
     * <li> a list of one default GroupRoleDto existing in the DB.</li>
     * </ul>
     */
    public static TestGroupDtoBuilder.TestGroupDtoBuilderBuilder builderWithAllDefaults() {
        return (TestGroupDtoBuilder.TestGroupDtoBuilderBuilder) TestGroupDtoBuilder.withDefaults()
                .withGroupUser(TestGroupUserDtoBuilder.withDefaults().build())
                .withGroupRole(TestGroupRoleDtoBuilder.withDefaults().build());
    }
}
