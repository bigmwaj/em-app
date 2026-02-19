package ca.bigmwaj.emapp.as.validator.xml.platform;

import ca.bigmwaj.emapp.as.builder.platform.ContactDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for ContactDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
@ActiveProfiles("test")
class ContactDtoValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void testContactDto_CreateWithValidData() {
        ContactDto dto = ContactDtoBuilder.builderWithAllDefaults().build();
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);
        // Should have no violations for valid data
        assertTrue(violations.isEmpty(), "Expected no violations for valid CREATE Contact");
    }

    @Test
    void testContactDto_CreateWithMissingFirstName() {
        ContactDto dto = ContactDtoBuilder.builderWithAllDefaults().build();
        dto.setFirstName(null); // Missing required field

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);
        // Should have violation for missing name
        assertFalse(violations.isEmpty(), "Expected violations for missing name");
        assertTrue(violations.stream()
                        .anyMatch(v -> "firstName".equals(v.getPropertyPath().toString())),
                "Expected violation on 'firstName' field");
    }

    @Test
    void testContactDto_CreateWithNameTooLong() {
        ContactDto dto = ContactDtoBuilder.builderWithAllDefaults().build();
        dto.setFirstName("A".repeat(50)); // Exceeds max length of 32

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        // Should have violation for name too long
        assertFalse(violations.isEmpty(), "Expected violations for name too long");
        assertTrue(violations.stream()
                        .anyMatch(v -> "firstName".equals(v.getPropertyPath().toString())),
                "Expected violation on 'firstName' field");
    }

    @Test
    void testContactDto_UpdateWithoutId() {
        ContactDto dto = ContactDtoBuilder.builder().withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(null); // Missing required ID for update

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        // Should have violation for missing ID
        assertFalse(violations.isEmpty(), "Expected violations for missing ID on UPDATE");
        assertTrue(violations.stream()
                        .anyMatch(v -> "id".equals(v.getPropertyPath().toString())),
                "Expected violation on 'id' field");
    }

    @Test
    void testContactDto_UpdateWithValidData() {
        ContactDto dto = ContactDtoBuilder.builder().withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(1L);

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE Contact");
    }

    @Test
    void testContactDto_BirthDateTooYoung() {
        ContactDto dto = ContactDtoBuilder.builder().withDefaults().build();
        dto.setBirthDate(LocalDate.now().minusYears(10)); // Exceeds max length of 100

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        // Should have violation for description too long
        assertFalse(violations.isEmpty(), "Expected violations for birthDate too young");
        assertTrue(violations.stream()
                        .anyMatch(v -> "birthDate".equals(v.getPropertyPath().toString())),
                "Expected violation on 'birthDate' field");
    }
}
