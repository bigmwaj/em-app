package ca.bigmwaj.emapp.as.validator.xml.platform;

import ca.bigmwaj.emapp.as.builder.platform.TestAccountContactDtoBuilder;
import ca.bigmwaj.emapp.as.builder.platform.TestAccountDtoBuilder;
import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.service.platform.AccountService;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import ca.bigmwaj.emapp.as.lvo.platform.AccountContactRoleLvo;
import ca.bigmwaj.emapp.as.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UsernameTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Integration test for AccountDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AccountDtoValidatorTest extends AbstractDtoValidatorTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDao accountDao;

    @BeforeEach
    void setUp(){
        accountDao.deleteAll();
    }

    @Test
    void testAccountDto_CreateFieldsValidation() {
        // The field 'name' is required and should have a max length of 32 characters
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withName(null).build(), "name");
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withName("").build(), "name");
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withName("A".repeat(33)).build(), "name");

        // The field 'description' is optional, but if provided, it should not exceed 256 characters
        assertNoViolations(TestAccountDtoBuilder.builderWithAllDefaults().withDescription(null).build());
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withDescription("A".repeat(257)).build(), "description");

        // The field 'status' is required and should not be 'ACTIVE'
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withStatus(null).build(), "status");
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withStatus(AccountStatusLvo.BLOCKED).build(), "status");

        // The field 'adminUsernameType' is required
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsernameType(null).build(), "adminUsernameType");

        // When the 'adminUsernameType' is 'PHONE', the 'adminUsernamePhoneIndicative' field is required
        // and should be supported by the platform
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsernameType(UsernameTypeLvo.PHONE)
                .withAdminUsernameTypePhoneIndicative(null).build(), "adminUsernameTypePhoneIndicative");
        assertThrowValidationConfigurationException(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsernameType(UsernameTypeLvo.PHONE)
                .withAdminUsernameTypePhoneIndicative("+44")
                .build(), "The indicative '+44' is not supported by PhoneRule yet");

        // When the 'adminUsernameType' is 'PHONE', the 'adminUsername' field should be a valid phone
        // number based on the provided 'adminUsernamePhoneIndicative'
        assertNoViolations(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsernameType(UsernameTypeLvo.PHONE)
                .withAdminUsername("4182552407").withAdminUsernameTypePhoneIndicative("+1").build());

        // When the 'adminUsernameType' is 'EMAIL', the 'adminUsername' field should be a valid email address
        assertNoViolations(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsername("a@test.com").build());
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsername("no-email").build(), "adminUsername");

        // The field 'adminUsername' is required and should have a max length of 16 characters
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsername(null).build(), "adminUsername");
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsername("A".repeat(17)).build(), "adminUsername");

        // The field 'adminUsername' should be unique. The user with username 'admin' already
        // exists in the system, so it should trigger a violation.
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAdminUsername("admin").withAdminUsernameType(UsernameTypeLvo.BASIC)
                .build(), "adminUsername");

        // The field 'accountContacts' is required and should not be empty. The role of the first should be 'PRINCIPAL'
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .clearAccountContacts().build(), "accountContacts");
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withAccountContact(TestAccountContactDtoBuilder.withDefaults()
                        .withRole(null).build()).build(), "role");
        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .clearAccountContacts().withAccountContact(TestAccountContactDtoBuilder.withDefaults()
                        .withRole(AccountContactRoleLvo.AGENT).build()).build(), "accountContacts");

        assertViolationsOnField(TestAccountDtoBuilder.builderWithAllDefaults()
                .withStatusReason("A".repeat(255)).build(), "statusReason");


    }

    @Transactional
    @Test
    void testAccountDto_Create() {
        var accountDto = TestAccountDtoBuilder.builderWithAllDefaults().build();
        var dto = accountService.create(accountDto);
        Assertions.assertNotNull(dto);
    }

    @Test
    void testAccountDto_ChangeStatusFieldsValidation() {
        // Full data
        assertNoViolations(TestAccountDtoBuilder.builderWithAllDefaults()
                .withId((short) 1)
                .withEditAction(EditActionLvo.CHANGE_STATUS).build());

        // Only required data
        assertNoViolations(TestAccountDtoBuilder.builder()
                .withId((short) 1)
                .withEditAction(EditActionLvo.CHANGE_STATUS)
                .withStatus(AccountStatusLvo.ACTIVE)
                .withStatusDate(LocalDateTime.now()).build());

        // Missing status
        assertViolationsOnField(TestAccountDtoBuilder.builder()
                .withId((short) 1)
                .withEditAction(EditActionLvo.CHANGE_STATUS)
                .withStatus(null).build(), "status");

        // Missing ID
        assertViolationsOnField(TestAccountDtoBuilder.builder()
                .withId(null).withEditAction(EditActionLvo.CHANGE_STATUS)
                .withStatus(AccountStatusLvo.ACTIVE).build(), "id");

        // Missing status date
        assertViolationsOnField(TestAccountDtoBuilder.builder()
                .withId((short) 1)
                .withEditAction(EditActionLvo.CHANGE_STATUS)
                .withStatusDate(null).build(), "statusDate");
    }


    @Test
    void testAccountDto_UpdateFieldsValidation() {
        // Full data
        assertNoViolations(TestAccountDtoBuilder.builderWithAllDefaults()
                .withId((short) 1)
                .withEditAction(EditActionLvo.UPDATE).build());

        // Only account data
        assertNoViolations(TestAccountDtoBuilder.withDefaults()
                .withId((short) 1)
                .withEditAction(EditActionLvo.UPDATE).build());

        // Let create an account for testing update
        var initialDto = TestAccountDtoBuilder.builderWithAllDefaults()
                .withAccountContact(TestAccountContactDtoBuilder.builderWithAllDefaults().build()).build();

        Assertions.assertEquals(2, initialDto.getAccountContacts().size());
        var createdDto = accountService.create(initialDto);




        createdDto.setName("newName");
        createdDto.setDescription("newDescription");
        createdDto.setEditAction(EditActionLvo.UPDATE);
        assertNoViolations(createdDto);

        var updatedDto = accountService.update(createdDto);

        Assertions.assertNotEquals(initialDto.getName(), updatedDto.getName());
        Assertions.assertNotEquals(initialDto.getDescription(), updatedDto.getDescription());

        //Assertions.assertEquals(2, updatedDto.getAccountContacts().size());

    }


}
