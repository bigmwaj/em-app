package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.builder.common.TestConstant;
import ca.bigmwaj.emapp.as.dto.platform.GroupUserDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleUserDto;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestGroupUserDtoBuilder extends GroupUserDto {

    /**
     * The associated user is supposed to have been created either in DB on in
     * the current transaction and it ID should have been updated in this building.
     */
    public static TestGroupUserDtoBuilder.TestGroupUserDtoBuilderBuilder withDefaults() {
        var existingUser = TestUserDtoBuilder.withDefaults().build();
        // create it or get it from DB and update the ID in the builder
        return TestGroupUserDtoBuilder.builder()
                .withCreatedBy(TestConstant.TEST_USER)
                .withUpdatedBy(TestConstant.TEST_USER)
                .withCreatedDate(LocalDateTime.now())
                .withUpdatedDate(LocalDateTime.now())
                .withEditAction(EditActionLvo.CREATE)
                .withUser(existingUser);
    }
}
