package ca.bigmwaj.emapp.as.validator.xml.platform;

import ca.bigmwaj.emapp.as.builder.platform.TestUserDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for UserDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserDtoValidatorTest extends AbstractDtoValidatorTest {

    @Autowired
    private Validator validator;

    UserDto validDto;

    @BeforeEach
    void setUp() {
        validDto = TestUserDtoBuilder.builderWithAllDefaults().build();
    }

    @Test
    void testUserDto_CreateWithFullValidData() {
        var violations = validator.validate(validDto);
        assertTrue(violations.isEmpty(), "Expected no violations for valid CREATE User");
    }

    @Test
    void testUserDto_CreateWithoutContact() {
        validDto.setContact(null);
        assertViolationsOnField(validDto, "contact");
    }

    @Test
    void testUserDto_CreateWithInconsistentOwnerType() {
        validDto.setOwnerType(OwnerTypeLvo.CORPORATE);
        validDto.getContact().setOwnerType(OwnerTypeLvo.ACCOUNT);
        assertViolationsOnField(validDto, "ownerType");
    }

    @Test
    void testUserDto_UpdateWithoutId() {
        UserDto dto = TestUserDtoBuilder.withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(null); // Missing required ID for update

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        // Should have violation for missing ID
        assertFalse(violations.isEmpty(), "Expected violations for missing ID on UPDATE");
        assertTrue(violations.stream()
                        .anyMatch(v -> "id".equals(v.getPropertyPath().toString())),
                "Expected violation on 'id' field");
    }

    @Test
    void testUserDto_UpdateWithValidData() {
        UserDto dto = TestUserDtoBuilder.withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId((short)1);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE User");
    }
}
