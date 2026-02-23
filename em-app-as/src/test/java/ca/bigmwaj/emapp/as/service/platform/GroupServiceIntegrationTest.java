package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.builder.platform.*;
import ca.bigmwaj.emapp.as.dao.platform.*;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.GroupDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.entity.platform.*;
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
class GroupServiceIntegrationTest extends AbstractDtoValidatorTest {

    @Autowired
    private GroupService service;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private GroupRoleDao groupRoleDao;

    @Autowired
    private GroupUserDao groupUserDao;

    @Autowired
    private RolePrivilegeDao rolePrivilegeDao;

    RoleDto existingRole;

    UserDto existingUser;

    @BeforeEach
    void setUp() {
        existingRole = TestRoleDtoBuilder.withDefaults().build();
        existingUser = TestUserDtoBuilder.builderWithAllDefaults().build();

        var createRole = roleDao.save(GlobalPlatformMapper.INSTANCE.toEntity(existingRole));
        existingRole = GlobalPlatformMapper.INSTANCE.toDto(createRole);
        existingUser = userService.create(existingUser);
    }

    private GroupDto buildGroupDto(RoleDto roleDto, UserDto userDto) {
        return TestGroupDtoBuilder.withDefaults()
                .clearGroupRoles()
                .withGroupRole(TestGroupRoleDtoBuilder.withDefaults().withRole(roleDto).build())
                .clearGroupUsers()
                .withGroupUser(TestGroupUserDtoBuilder.withDefaults().withUser(userDto).build())
                .build();
    }

    @Test
    void test_CreateRole() {

        GroupDto groupDto = buildGroupDto(existingRole, existingUser);

        // When all required fields are populated and valid, validation should pass with no violations
        assertNoViolations(groupDto);

        // When the user is missing from any GroupUserDto, validation should fail with a violation on the "user" field
        groupDto.getGroupUsers().get(0).setUser(null);
        assertViolationsOnField(groupDto, "user", "The field 'user' cannot be null.");

        groupDto.getGroupUsers().get(0).setUser(existingUser); // reset to valid state for next test

        // When the user ID in any GroupUserDto is null,
        // validation should fail with a violation on the "id"
        // field since the user must exist in the DB
        short existingUserId = existingUser.getId();
        existingUser.setId(null);
        assertViolationsOnField(groupDto, "id", "The field 'id' cannot be null.");

        // When the user ID in any GroupUserDto does not exist in the DB,
        // validation should fail with a violation on the "id"
        existingUser.setId((short) (existingUserId + 100));
        assertViolationsOnField(groupDto, "id", "User with ID %s does not exist." .formatted(existingUser.getId()));

        existingUser.setId(existingUserId); // reset to valid state for next test

        // When the role is missing from any GroupRoleDto, validation should fail with a violation
        groupDto.getGroupRoles().get(0).setRole(null);
        assertViolationsOnField(groupDto, "role", "The field 'role' cannot be null.");

        groupDto.getGroupRoles().get(0).setRole(existingRole); // reset to valid state for next test

        // When the role ID in GroupRoleDto is null,
        // validation should fail with a violation on the "id"
        // field since the role must exist in the DB
        short existingRoleId = existingRole.getId();
        existingRole.setId(null);
        assertViolationsOnField(groupDto, "id", "The field 'id' cannot be null.");

        // When the group ID in GroupRoleDto does not exist in the DB,
        // validation should fail with a violation on the "id"
        existingRole.setId((short) (existingRoleId + 100));
        assertViolationsOnField(groupDto, "id", "Role with ID %s does not exist." .formatted(existingRole.getId()));

        existingRole.setId(existingRoleId); // reset to valid state for next test

        // When the list of RoleUserDto is null, validation should pass with no violations
        // since the list is not required
        groupDto.setGroupUsers(null);
        assertNoViolations(groupDto);

        // When the list of GroupRoleDto is null, validation should pass with no
        // violations since the list is not required
        groupDto.setGroupRoles(null);
        assertNoViolations(groupDto);

        groupDto = buildGroupDto(existingRole, existingUser);
        groupDto = service.create(groupDto);
        assertNotNull(groupDto);
        // Service never returns users after creation.
        assertNull(groupDto.getGroupUsers());

        // Service never returns roles after creation.
        assertNull(groupDto.getGroupRoles());

        // Check the created role has been assigned to existing user
        var assignedToUser = groupUserDao.findByGroupId(groupDto.getId()).stream()
                .map(GroupUserEntity::getUser)
                .map(UserEntity::getId)
                .anyMatch(existingUser.getId()::equals);
        assertTrue(assignedToUser);

        // Check the created group has been assigned to existing user
        var assignedToRole = groupRoleDao.findByGroupId(groupDto.getId()).stream()
                .map(GroupRoleEntity::getRole)
                .map(RoleEntity::getId)
                .anyMatch(existingRole.getId()::equals);
        assertTrue(assignedToRole);

        // Role name is always unique no matter the case
        groupDto = buildGroupDto(existingRole, existingUser);

        assertViolationsOnField(groupDto, "name", "The group '%s' already exists." .formatted(groupDto.getName()));
    }

    @Test
    void test_UpdateRole() {

        GroupDto existingGroup = buildGroupDto(existingRole, existingUser);
        existingGroup = service.create(existingGroup);

        existingGroup.setEditAction(EditActionLvo.UPDATE);
        assertNoViolations(existingGroup);

        existingGroup.setDescription(existingGroup.getDescription() + "Updated");
        existingGroup = service.update(existingGroup);

        // The assign user list should be preserved.
        var assignedToUsers = groupUserDao.findByGroupId(existingGroup.getId()).isEmpty();
        assertFalse(assignedToUsers);

        // The assign user list should be preserved.
        var assignedToRoles = groupRoleDao.findByGroupId(existingGroup.getId()).isEmpty();
        assertFalse(assignedToRoles);
    }

    @Test
    void test_DeleteRole() {
        GroupDto existingGroup = buildGroupDto(existingRole, existingUser);
        existingGroup = service.create(existingGroup);

        service.deleteById(existingGroup.getId());

        // The assign role list should be removed.
        var assignedToRole = groupRoleDao.findByGroupId(existingGroup.getId()).isEmpty();
        assertTrue(assignedToRole);

        // The assign user list should be removed.
        var assignedToUser = groupUserDao.findByGroupId(existingGroup.getId()).isEmpty();
        assertTrue(assignedToUser);
    }

}
