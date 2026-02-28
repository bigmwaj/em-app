package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.builder.common.TestConstant;
import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestRolePrivilegeDtoBuilder extends RolePrivilegeDto {

    /**
     * The associated privilege is supposed to have been created either in DB on in
     * the current transaction and it ID should have been updated in this building.
     */
    public static TestRolePrivilegeDtoBuilder.TestRolePrivilegeDtoBuilderBuilder withDefaults() {
        var existingPrivilege = TestPrivilegeDtoBuilder.withDefaults().build();
        // create it or get it from DB and update the ID in the builder
        return TestRolePrivilegeDtoBuilder.builder()
                .withCreatedBy(TestConstant.TEST_USER)
                .withUpdatedBy(TestConstant.TEST_USER)
                .withCreatedDate(LocalDateTime.now())
                .withUpdatedDate(LocalDateTime.now())
                .withEditAction(EditActionLvo.CREATE)
                .withPrivilege(existingPrivilege);
    }
}
