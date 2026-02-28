package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.builder.platform.TestRoleDtoBuilder;
import ca.bigmwaj.emapp.as.builder.platform.TestUserDtoBuilder;
import ca.bigmwaj.emapp.as.builder.platform.TestUserRoleDtoBuilder;
import ca.bigmwaj.emapp.as.dao.platform.RoleDao;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.entity.platform.RoleEntity;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for UserService using H2 in-memory database.
 * Tests CRUD operations with real database interactions.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest extends AbstractDtoValidatorTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    private RoleDto existingRole;

    private RoleDto existingAccountRole;

    @BeforeEach
    void setUp() {

        existingRole = TestRoleDtoBuilder.withDefaults().build();
        existingAccountRole = TestRoleDtoBuilder.withDefaults()
                .withOwnerType(OwnerTypeLvo.ACCOUNT)
                .withName("ACCOUNT-ROLE")
                .build();
        RoleEntity entity = roleDao.save(GlobalPlatformMapper.INSTANCE.toEntity(existingRole));
        RoleEntity entity2 = roleDao.save(GlobalPlatformMapper.INSTANCE.toEntity(existingAccountRole));
        existingRole = GlobalPlatformMapper.INSTANCE.toDto(entity);
        existingAccountRole = GlobalPlatformMapper.INSTANCE.toDto(entity2);
    }

    @Test
    void test_CreateUser() {
        UserDto dto = TestUserDtoBuilder.builderWithAllDefaults().clearUserRoles()
                .withUserRole(TestUserRoleDtoBuilder.withDefaults().withRole(existingRole).build())
                .build();
        assertNoViolations(dto);

        // Only CORPORATE owner type is tested here.
        assertEquals(OwnerTypeLvo.CORPORATE, dto.getOwnerType());

        dto.setOwnerType(null);
        assertViolationsOnField(dto, "ownerType");

        dto.setOwnerType(OwnerTypeLvo.ACCOUNT); // Inconsistent with contact's owner type
        assertViolationsOnField(dto, "ownerType");

        dto.getUserRoles().get(0).setRole(existingAccountRole);
        assertViolationsOnField(dto, "ownerType");

        dto.getUserRoles().get(0).setRole(existingRole); // Fix role to be consistent with user owner type

        dto.getContact().setOwnerType(OwnerTypeLvo.ACCOUNT);
        assertViolationsOnField(dto, "ownerType");

        dto.getContact().setOwnerType(OwnerTypeLvo.CORPORATE);

        dto = service.create(dto);

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
        dto.setId((short) 1);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        // Should have no violations for valid update
        assertTrue(violations.isEmpty(), "Expected no violations for valid UPDATE User");
    }

}
