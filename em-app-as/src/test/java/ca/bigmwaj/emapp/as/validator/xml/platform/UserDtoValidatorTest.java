package ca.bigmwaj.emapp.as.validator.xml.platform;

import ca.bigmwaj.emapp.as.builder.platform.UserDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for UserDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
class UserDtoValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void testUserDto_CreateWithValidData() {
        UserDto dto = UserDtoBuilder.builderWithAllDefaults().build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no violations for valid CREATE User");
    }

    @Test
    void testUserDto_CreateWithMissingContact() {
        UserDto dto = UserDtoBuilder.builderWithAllDefaults().build();
        dto.setContact(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations for user without contact");
        assertTrue(violations.stream()
                        .anyMatch(v -> "contact".equals(v.getPropertyPath().toString())),
                "Expected violation on 'contact' field");
    }

    @Test
    void testUserDto_CreateWithMissingContactEmail() {
        UserDto dto = UserDtoBuilder.builderWithAllDefaults().build();
        dto.getContact().setEmails(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations for user without contact email");
        assertTrue(violations.stream()
                        .anyMatch(v -> "emails".equals(v.getPropertyPath().toString())),
                "Expected violation on 'emails' field");
    }

    @Test
    void testUserDto_CreateWithContactWithEmptyEmailsList() {
        UserDto dto = UserDtoBuilder.builderWithAllDefaults().build();
        dto.getContact().setEmails(Collections.emptyList());
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations for user without contact email");
        assertTrue(violations.stream()
                        .anyMatch(v -> "emails".equals(v.getPropertyPath().toString())),
                "Expected violation on 'emails' field");
    }
    
    @Test
    void testUserDto_CreateWithMissingFirstName() {
        UserDto dto = UserDtoBuilder.builderWithAllDefaults().build();
        dto.setUsername(null); // Missing required field

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        // Should have violation for missing name
        assertFalse(violations.isEmpty(), "Expected violations for missing name");
        assertTrue(violations.stream()
                        .anyMatch(v -> "username".equals(v.getPropertyPath().toString())),
                "Expected violation on 'username' field");
    }

    @Test
    void testUserDto_CreateWithNameTooLong() {
        UserDto dto = UserDtoBuilder.builderWithAllDefaults().build();
        dto.setUsername("A".repeat(50)); // Exceeds max length of 32

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        // Should have violation for name too long
        assertFalse(violations.isEmpty(), "Expected violations for name too long");
        assertTrue(violations.stream()
                        .anyMatch(v -> "username".equals(v.getPropertyPath().toString())),
                "Expected violation on 'username' field");
    }

    @Test
    void testUserDto_UpdateWithoutId() {
        UserDto dto = UserDtoBuilder.builder().withDefaults().build();
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
        UserDto dto = UserDtoBuilder.builder().withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(1L);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE User");
    }
}
