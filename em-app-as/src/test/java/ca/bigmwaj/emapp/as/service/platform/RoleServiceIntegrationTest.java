package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.builder.platform.*;
import ca.bigmwaj.emapp.as.dao.platform.PrivilegeDao;
import ca.bigmwaj.emapp.as.dao.platform.RolePrivilegeDao;
import ca.bigmwaj.emapp.as.dao.platform.UserRoleDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.entity.platform.PrivilegeEntity;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegeEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserRoleEntity;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleServiceIntegrationTest extends AbstractDtoValidatorTest {

    @Autowired
    private RoleService service;

    @Autowired
    private UserService userService;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RolePrivilegeDao rolePrivilegeDao;

    PrivilegeDto existingPrivilege;

    UserDto existingUser;

    @BeforeEach
    void setUp() {
        existingPrivilege = TestPrivilegeDtoBuilder.withDefaults().build();
        existingUser = TestUserDtoBuilder.builderWithAllDefaults().build();
        var createPrivilege = privilegeDao.save(GlobalPlatformMapper.INSTANCE.toEntity(existingPrivilege));
        existingPrivilege = GlobalPlatformMapper.INSTANCE.toDto(createPrivilege);
        existingUser = userService.create(existingUser);
    }

    private RoleDto buildRoleDto(PrivilegeDto privilegeDto, UserDto userDto) {
        return TestRoleDtoBuilder.withDefaults()
                .clearRolePrivileges()
                .withRolePrivilege(TestRolePrivilegeDtoBuilder.withDefaults().withPrivilege(privilegeDto).build())
                .clearRoleUsers()
                .withRoleUser(TestRoleUserDtoBuilder.withDefaults().withUser(userDto).build())
                .build();
    }

    @Test
    void test_CreateRole() {

        RoleDto roleDto;

        roleDto = buildRoleDto(existingPrivilege, existingUser);

        // When all required fields are populated and valid, validation should pass with no violations
        assertNoViolations(roleDto);

        // When the user is missing from any RoleUserDto, validation should fail with a violation on the "user" field
        roleDto.getRoleUsers().get(0).setUser(null);
        assertViolationsOnField(roleDto, "user", "The field 'user' cannot be null.");

        roleDto.getRoleUsers().get(0).setUser(existingUser); // reset to valid state for next test

        // When the user ID in any RoleUserDto is null,
        // validation should fail with a violation on the "user.id"
        // field since the user must exist in the DB
        short existingUserId = existingUser.getId();
        existingUser.setId(null);
        assertViolationsOnField(roleDto, "id", "The field 'id' cannot be null.");

        // When the user ID in any RoleUserDto does not exist in the DB,
        // validation should fail with a violation on the "user.id"
        existingUser.setId((short) (existingUserId + 100));
        assertViolationsOnField(roleDto, "id", "User with ID %s does not exist.".formatted(existingUser.getId()));

        existingUser.setId(existingUserId); // reset to valid state for next test

        // When the privilege is missing from any RolePrivilegeDto, validation should fail with a violation
        roleDto.getRolePrivileges().get(0).setPrivilege(null);
        assertViolationsOnField(roleDto, "privilege", "The field 'privilege' cannot be null.");

        roleDto.getRolePrivileges().get(0).setPrivilege(existingPrivilege); // reset to valid state for next test

        // When the privilege ID in RolePrivilegeDto is null,
        // validation should fail with a violation on the "privilege.id"
        // field since the privilege must exist in the DB
        short existingPrivilegeId = existingPrivilege.getId();
        existingPrivilege.setId(null);
        assertViolationsOnField(roleDto, "id", "The field 'id' cannot be null.");

        // When the privilege ID in RolePrivilegeDto does not exist in the DB,
        // validation should fail with a violation on the "privilege.id"
        existingPrivilege.setId((short) (existingPrivilegeId + 100));
        assertViolationsOnField(roleDto, "id", "Privilege with ID %s does not exist.".formatted(existingPrivilege.getId()));

        existingPrivilege.setId(existingPrivilegeId); // reset to valid state for next test

        // When the list of RoleUserDto is null, validation should pass with no violations
        // since the list is not required
        roleDto.setRoleUsers(null);
        assertNoViolations(roleDto);

        // When the list of RolePrivilegeDto is null, validation should pass with no
        // violations since the list is not required
        roleDto.setRolePrivileges(null);
        assertNoViolations(roleDto);

        roleDto = buildRoleDto(existingPrivilege, existingUser);
        roleDto = service.create(roleDto);
        assertNotNull(roleDto);
        // Service never returns users after creation.
        assertNull(roleDto.getRoleUsers());

        // Service never returns privileges after creation.
        assertNull(roleDto.getRolePrivileges());

        // Check the created role has been assigned to existing user
        var assignedToUser = userRoleDao.findByRoleId(roleDto.getId()).stream()
                .map(UserRoleEntity::getUser)
                .map(UserEntity::getId)
                .anyMatch(existingUser.getId()::equals);
        assertTrue(assignedToUser);

        // Check the created role has been assigned to existing user
        var assignedToPrivilege = rolePrivilegeDao.findByRoleId(roleDto.getId()).stream()
                .map(RolePrivilegeEntity::getPrivilege)
                .map(PrivilegeEntity::getId)
                .anyMatch(existingPrivilege.getId()::equals);
        assertTrue(assignedToPrivilege);

        // Role name is always unique no matter the case
        roleDto = buildRoleDto(existingPrivilege, existingUser);

        assertViolationsOnField(roleDto, "name", "The rule '%s' is already.".formatted(roleDto.getName()));
    }

    @Test
    void test_UpdateRole() {

        RoleDto existingRole = buildRoleDto(existingPrivilege, existingUser);
        existingRole = service.create(existingRole);

        existingRole.setEditAction(EditActionLvo.UPDATE);
        assertNoViolations(existingRole);

        existingRole.setDescription( existingRole.getDescription() + "Updated");
        existingRole = service.update(existingRole);

        // The assign privilege list should be preserved.
        var assignedToPrivilege = rolePrivilegeDao.findByRoleId(existingRole.getId()).isEmpty();
        assertFalse(assignedToPrivilege);
    }

    @Test
    void test_DeleteRole() {
        RoleDto existingRole = buildRoleDto(existingPrivilege, existingUser);
        existingRole = service.create(existingRole);

        service.deleteById(existingRole.getId());

        // The assign privilege list should be removed.
        var assignedToPrivilege = rolePrivilegeDao.findByRoleId(existingRole.getId()).isEmpty();
        assertTrue(assignedToPrivilege);

        // The assign user list should be removed.
        var assignedToUser = userRoleDao.findByRoleId(existingRole.getId()).isEmpty();
        assertTrue(assignedToUser);
    }

}
