package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.dao.platform.*;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleUserDto;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RoleMapper extends AbstractMapper {
    private final RoleDao roleDao;
    private final PrivilegeDao privilegeDao;
    private final RolePrivilegeDao rolePrivilegeDao;
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;

    @Autowired
    public RoleMapper(RoleDao roleDao, PrivilegeDao privilegeDao, RolePrivilegeDao rolePrivilegeDao, UserDao userDao, UserRoleDao userRoleDao) {
        this.roleDao = roleDao;
        this.privilegeDao = privilegeDao;
        this.rolePrivilegeDao = rolePrivilegeDao;
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
    }

    public RoleEntity mappingForCreate(RoleDto dto) {
        var entity = new RoleEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return beforeCreateHistEntity(entity);
    }

    public RoleEntity mappingForUpdate(RoleDto dto) {
        var entity = roleDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + dto.getId()));
        entity.setDescription(dto.getDescription());
        return beforeUpdateHistEntity(entity);
    }

    public RoleEntity mappingForDelete(RoleDto dto) {
        return roleDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + dto.getId()));
    }

    public RolePrivilegeEntity mappingForCreate(RoleEntity entity, RolePrivilegeDto dto) {
        var privilege = dto.getPrivilege();
        Objects.requireNonNull(privilege, "privilege must not be null");
        Objects.requireNonNull(privilege.getId(), "privilege ID must not be null");

        var privilegeEntity = privilegeDao.findById(privilege.getId()).orElseThrow(() -> new IllegalArgumentException("Privilege not found with id: " + privilege.getId()));
        var child = new RolePrivilegeEntity();
        child.setRole(entity);
        child.setPrivilege(privilegeEntity);
        return beforeCreateHistEntity(child);
    }

    public RolePrivilegeEntity mappingForDelete(RoleEntity entity, RolePrivilegeDto dto) {
        var privilege = dto.getPrivilege();
        Objects.requireNonNull(privilege, "privilege must not be null");
        Objects.requireNonNull(privilege.getId(), "privilege ID must not be null");
        return rolePrivilegeDao.findById(new RolePrivilegePK(entity.getId(), privilege.getId())).orElseThrow(() -> new IllegalArgumentException("RolePrivilege not found with role id: " + entity.getId() + " and privilege id: " + privilege.getId()));
    }

    // End of RolePrivilege mapping methods

    // RoleUser mapping methods. User should exist in DB

    public UserRoleEntity mappingForCreate(RoleEntity entity, RoleUserDto dto) {
        var user = dto.getUser();
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(user.getId(), "user ID must not be null");
        var userEntity = userDao.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));
        var child = new UserRoleEntity();
        child.setRole(entity);
        child.setUser(userEntity);

        return beforeCreateHistEntity(child);
    }

    public UserRoleEntity mappingForDelete(RoleEntity entity, RoleUserDto dto) {
        var user = dto.getUser();
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(user.getId(), "user ID must not be null");
        return userRoleDao.findById(new UserRolePK(user.getId(), entity.getId())).orElseThrow(() -> new IllegalArgumentException("UserRole not found with user id: " + user.getId() + " and role id: " + entity.getId()));
    }
}
