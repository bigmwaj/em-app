package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.dao.platform.*;
import ca.bigmwaj.emapp.as.dto.platform.GroupDto;
import ca.bigmwaj.emapp.as.dto.platform.GroupRoleDto;
import ca.bigmwaj.emapp.as.dto.platform.GroupUserDto;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GroupMapper extends AbstractMapper {

    private final GroupDao groupDao;
    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public GroupMapper(GroupDao groupDao, UserDao userDao, RoleDao roleDao) {
        this.groupDao = groupDao;
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    public GroupEntity mappingForCreate(GroupDto dto) {
        var entity = new GroupEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return beforeCreateHistEntity(entity);
    }

    public GroupEntity mappingForUpdate(GroupDto dto) {
        var entity = groupDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + dto.getId()));
        entity.setDescription(dto.getDescription());
        return beforeUpdateHistEntity(entity);
    }

    public GroupUserEntity mappingForCreate(GroupEntity entity, GroupUserDto dto) {
        var user = dto.getUser();
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(user.getId(), "User ID must not be null");
        var userEntity = userDao.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));

        var child = new GroupUserEntity();
        child.setGroup(entity);
        child.setUser(userEntity);
        return beforeCreateHistEntity(child);
    }

    public GroupRoleEntity mappingForCreate(GroupEntity entity, GroupRoleDto dto) {
        var role = dto.getRole();
        Objects.requireNonNull(role, "Role must not be null");
        Objects.requireNonNull(role.getId(), "Role ID must not be null");

        var roleEntity = roleDao.findById(role.getId()).orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + role.getId()));

        var child = new GroupRoleEntity();
        child.setGroup(entity);
        child.setRole(roleEntity);
        return beforeCreateHistEntity(child);
    }
}
