package ca.bigmwaj.emapp.as.validator.xml.platform;

import ca.bigmwaj.emapp.as.builder.platform.TestContactDtoBuilder;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for ContactDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
@ActiveProfiles("test")
class ContactDtoValidatorTest extends AbstractDtoValidatorTest {

    ContactDto validDto;

    @BeforeEach
    void setUp() {
        validDto = TestContactDtoBuilder.builderWithAllDefaults().build();
    }

    @Test
    void testContactDto_CreateWithValidData() {
        var violations = validator.validate(validDto);
        assertTrue(violations.isEmpty(), "Expected no violations for valid contact Contact");
    }

    @Test
    void testContactDto_CreateWithDataWithNullAddressList() {
        validDto.setAddresses(null);
        assertNoViolations(validDto);
    }

    @Test
    void testContactDto_CreateWithDataWithEmptyAddressList() {
        validDto.setAddresses(Collections.emptyList());
        assertNoViolations(validDto);
    }

    @Test
    void testContactDto_CreateWithDataWithNullEmailList() {
        validDto.setEmails(null);
        assertViolationsOnField(validDto, "emails");
    }

    @Test
    void testContactDto_CreateWithDataWithEmptyEmailList() {
        validDto.setEmails(Collections.emptyList());
        assertViolationsOnField(validDto, "emails");
    }

    @Test
    void testContactDto_CreateWithDataWithNullPhoneList() {
        validDto.setPhones(null);
        assertViolationsOnField(validDto, "phones");
    }

    @Test
    void testContactDto_CreateWithDataWithEmptyPhoneList() {
        validDto.setPhones(Collections.emptyList());
        assertViolationsOnField(validDto, "phones");
    }

    @Test
    void testContactDto_CreateWithMissingFirstName() {
        validDto.setFirstName(null); // Missing required field
        assertViolationsOnField(validDto, "firstName");
    }

    @Test
    void testContactDto_CreateWithNameTooLong() {
        validDto.setFirstName("A".repeat(50)); // Exceeds max length of 32
        assertViolationsOnField(validDto, "firstName");
    }

    @Test
    void testContactDto_UpdateWithoutId() {
        ContactDto dto = TestContactDtoBuilder.withDefaults().build();
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
        ContactDto dto = TestContactDtoBuilder.withDefaults().build();
        dto.setEditAction(EditActionLvo.UPDATE);
        dto.setId(1L);

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE Contact");
    }

    @Test
    void testContactDto_BirthDateTooYoung() {
        ContactDto dto = TestContactDtoBuilder.withDefaults().build();
        dto.setBirthDate(LocalDate.now().minusYears(10)); // Exceeds max length of 100

        Set<ConstraintViolation<ContactDto>> violations = validator.validate(dto);

        // Should have violation for description too long
        assertFalse(violations.isEmpty(), "Expected violations for birthDate too young");
        assertTrue(violations.stream()
                        .anyMatch(v -> "birthDate".equals(v.getPropertyPath().toString())),
                "Expected violation on 'birthDate' field");
    }
}
