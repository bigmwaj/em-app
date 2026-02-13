package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.dto.platform.AccountContactDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.AccountDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDtoBuilder;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
class SpringDtoValidatorIntegrationTest {

    @Autowired
    private Validator validator;

    @Test
    void testAccountDto_CreateWithValidData() {
        AccountDto dto = AccountDtoBuilder
                .builder()
                .withDefaults()
                .withAccountContactBuilders(
                        AccountContactDtoBuilder.builder().withDefaults()
                                .withContactBuilder(ContactDtoBuilder.builder().withDefaults())
                ).build();

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        
        // Should have no violations for valid data
        assertTrue(violations.isEmpty(), "Expected no violations for valid CREATE account");
    }

    @Test
    void testAccountDto_CreateWithMissingName() {
        var dto = new AccountDto();
        dto.setEditAction(EditActionLvo.CREATE);
        dto.setName(null); // Missing required field
        dto.setStatus(AccountStatusLvo.ACTIVE);

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        
        // Should have violation for missing name
        assertFalse(violations.isEmpty(), "Expected violations for missing name");
        assertTrue(violations.stream()
            .anyMatch(v -> "name".equals(v.getPropertyPath().toString())),
            "Expected violation on 'name' field");
    }

    @Test
    void testAccountDto_CreateWithNameTooLong() {
        AccountDto dto = new AccountDto();
        dto.setEditAction(EditActionLvo.CREATE);
        dto.setName("A".repeat(50)); // Exceeds max length of 32
        dto.setStatus(AccountStatusLvo.ACTIVE);

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        
        // Should have violation for name too long
        assertFalse(violations.isEmpty(), "Expected violations for name too long");
        assertTrue(violations.stream()
            .anyMatch(v -> "name".equals(v.getPropertyPath().toString())),
            "Expected violation on 'name' field");
    }

    @Test
    void testAccountDto_UpdateWithoutId() {
        AccountDto dto = new AccountDto();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(null); // Missing required ID for update
        dto.setName("Updated Account");
        dto.setStatus(AccountStatusLvo.ACTIVE);

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        
        // Should have violation for missing ID
        assertFalse(violations.isEmpty(), "Expected violations for missing ID on UPDATE");
        assertTrue(violations.stream()
            .anyMatch(v -> "id".equals(v.getPropertyPath().toString())),
            "Expected violation on 'id' field");
    }

    @Test
    void testAccountDto_UpdateWithValidData() {
        AccountDto dto = new AccountDto();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(1L);
        dto.setName("Updated Account");
        dto.setStatus(AccountStatusLvo.ACTIVE);

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        
        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE account");
    }

    @Test
    void testAccountDto_MissingStatus() {
        AccountDto dto = new AccountDto();
        dto.setEditAction(EditActionLvo.CREATE);
        dto.setName("Test Account");
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
        AccountDto dto = new AccountDto();
        dto.setEditAction(EditActionLvo.CREATE);
        dto.setName("Test Account");
        dto.setDescription("A".repeat(150)); // Exceeds max length of 100
        dto.setStatus(AccountStatusLvo.ACTIVE);

        Set<ConstraintViolation<AccountDto>> violations = validator.validate(dto);
        
        // Should have violation for description too long
        assertFalse(violations.isEmpty(), "Expected violations for description too long");
        assertTrue(violations.stream()
            .anyMatch(v -> "description".equals(v.getPropertyPath().toString())),
            "Expected violation on 'description' field");
    }
}
