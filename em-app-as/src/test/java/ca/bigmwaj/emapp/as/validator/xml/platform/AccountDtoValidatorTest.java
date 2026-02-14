package ca.bigmwaj.emapp.as.validator.xml.platform;

import ca.bigmwaj.emapp.as.builder.platform.AccountDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for AccountDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
class AccountDtoValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void testAccountDto_CreateWithValidData() {
        AccountDto dto = AccountDtoBuilder.builderWithAllDefaults().build();
        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        // Should have no violations for valid data
        assertTrue(violations.isEmpty(), "Expected no violations for valid CREATE account");
    }

    @Test
    void testAccountDto_CreateWithMissingName() {
        AccountDto dto = AccountDtoBuilder.builderWithAllDefaults().build();
        dto.setName(null); // Missing required field

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        // Should have violation for missing name
        assertFalse(violations.isEmpty(), "Expected violations for missing name");
        assertTrue(violations.stream()
                        .anyMatch(v -> "name".equals(v.getPropertyPath().toString())),
                "Expected violation on 'name' field");
    }

    @Test
    void testAccountDto_CreateWithNameTooLong() {
        AccountDto dto = AccountDtoBuilder.builderWithAllDefaults().build();
        dto.setName("A".repeat(50)); // Exceeds max length of 32

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);

        // Should have violation for name too long
        assertFalse(violations.isEmpty(), "Expected violations for name too long");
        assertTrue(violations.stream()
                        .anyMatch(v -> "name".equals(v.getPropertyPath().toString())),
                "Expected violation on 'name' field");
    }

    @Test
    void testAccountDto_UpdateWithoutId() {
        AccountDto dto = AccountDtoBuilder.builder().withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(null); // Missing required ID for update

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);

        // Should have violation for missing ID
        assertFalse(violations.isEmpty(), "Expected violations for missing ID on UPDATE");
        assertTrue(violations.stream()
                        .anyMatch(v -> "id".equals(v.getPropertyPath().toString())),
                "Expected violation on 'id' field");
    }

    @Test
    void testAccountDto_UpdateWithValidData() {
        AccountDto dto = AccountDtoBuilder.builder().withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(1L);

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);

        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE account");
    }

    @Test
    void testAccountDto_MissingStatus() {
        AccountDto dto = AccountDtoBuilder.builder().withDefaults().build();
        dto.setStatus(null); // Missing required status

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);

        // Should have violation for missing status
        assertFalse(violations.isEmpty(), "Expected violations for missing status");
        assertTrue(violations.stream()
                        .anyMatch(v -> "status".equals(v.getPropertyPath().toString())),
                "Expected violation on 'status' field");
    }

    @Test
    void testAccountDto_DescriptionTooLong() {
        AccountDto dto = AccountDtoBuilder.builder().withDefaults().build();
        dto.setDescription("A".repeat(150)); // Exceeds max length of 100

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);

        // Should have violation for description too long
        assertFalse(violations.isEmpty(), "Expected violations for description too long");
        assertTrue(violations.stream()
                        .anyMatch(v -> "description".equals(v.getPropertyPath().toString())),
                "Expected violation on 'description' field");
    }
}
