package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.*;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.entity.platform.*;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import ca.bigmwaj.emapp.as.service.ServiceException;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class GroupService extends AbstractMainService<GroupDto, GroupEntity, Short> {

    @Autowired
    private GroupDao dao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private GroupRoleDao groupRoleDao;

    @Autowired
    private GroupUserDao groupUserDao;

    @Autowired
    private UserDao userDao;

    public GroupDto create(GroupDto dto) {
        try {
            GroupEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
            beforeCreateHistEntity(entity);

            // Add roles to the group
            List<GroupRoleEntity> groupRoles = entity.getGroupRoles();
            if (groupRoles != null && !groupRoles.isEmpty()) {
                for (GroupRoleEntity groupRoleEntity : groupRoles) {
                    groupRoleEntity.setGroup(entity);
                    Short roleId = groupRoleEntity.getRole().getId();
                    RoleEntity roleEntity = roleDao.findById(roleId)
                            .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + roleId));
                    groupRoleEntity.setRole(roleEntity);
                    beforeCreateHistEntity(groupRoleEntity);
                }
            }

            // Add users to the group
            List<GroupUserEntity> groupUsers = entity.getGroupUsers();
            if (groupUsers != null && !groupUsers.isEmpty()) {
                for (GroupUserEntity groupUserEntity : groupUsers) {
                    groupUserEntity.setGroup(entity);
                    Short userId = groupUserEntity.getUser().getId();
                    UserEntity userEntity = userDao.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
                    groupUserEntity.setUser(userEntity);
                    beforeCreateHistEntity(groupUserEntity);
                }
            }

            var createdRole = dao.save(entity);

            return GlobalPlatformMapper.INSTANCE.toDto(createdRole);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to create Group: " + e.getMessage(), e);
        }
    }

    private GroupRoleEntity init(GroupRoleDto dto) {
        GroupRoleEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        Objects.requireNonNull(entity.getRole(), "Role cannot be null in GroupRoleEntity after mapping from GroupRoleDto.");

        Short roleId = entity.getRole().getId();
        Objects.requireNonNull(roleId, "RoleId cannot be null after mapping from GroupRoleDto.");

        RoleEntity role = roleDao.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + roleId));
        entity.setRole(role);
        beforeCreateHistEntity(entity);
        return entity;
    }

    private GroupUserEntity init(GroupUserDto dto) {
        GroupUserEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        Objects.requireNonNull(entity.getUser(), "User cannot be null in GroupUserEntity after mapping from GroupUserDto.");

        Short userId = entity.getUser().getId();
        Objects.requireNonNull(userId, "UserId cannot be null after mapping from GroupUserDto.");

        UserEntity user = userDao.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        entity.setUser(user);
        beforeCreateHistEntity(entity);
        return entity;
    }

    public SearchResultDto<GroupUserDto> findGroupUsers(Short groupId) {
        Objects.requireNonNull(groupId, "Role ID cannot be null for finding group users.");
        List<GroupUserDto> result = groupUserDao.findByGroupId(groupId).stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();
        return new SearchResultDto<>(result);
    }

    public SearchResultDto<GroupRoleDto> findGroupRoles(Short groupId) {
        List<GroupRoleDto> result = groupRoleDao.findByGroupId(groupId).stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();
        return new SearchResultDto<>(result);
    }

    public GroupDto update(GroupDto dto) {
        try {
            Objects.requireNonNull(dto, "GroupDto cannot be null in GroupDto.");

            Short groupId = dto.getId();
            Objects.requireNonNull(groupId, "Group ID cannot be null for update operation.");

            GroupEntity entity = dao.findById(groupId)
                    .orElseThrow(() -> new NoSuchElementException("Group not found with id: " + groupId));

            entity.setDescription(dto.getDescription());
            beforeUpdateHistEntity(entity);

            List<GroupRoleEntity> rolesToAdd = new ArrayList<>();
            List<GroupRolePK> rolesToDelete = new ArrayList<>();

            if (dto.getGroupRoles() != null && !dto.getGroupRoles().isEmpty()) {
                rolesToAdd = dto.getGroupRoles().stream()
                        .filter(AbstractBaseDto::isCreateAction)
                        .map(this::init)
                        .toList();

                rolesToDelete = dto.getGroupRoles().stream()
                        .filter(AbstractBaseDto::isDeleteAction)
                        .map(GlobalPlatformMapper.INSTANCE::toEntity)
                        .map(GroupRolePK::new)
                        .toList();

            }

            List<GroupUserEntity> usersToAdd = new ArrayList<>();
            List<GroupUserPK> usersToDelete = new ArrayList<>();

            if (dto.getGroupUsers() != null && !dto.getGroupUsers().isEmpty()) {
                usersToAdd = dto.getGroupUsers().stream()
                        .filter(AbstractBaseDto::isCreateAction)
                        .map(this::init)
                        .toList();

                usersToDelete = dto.getGroupUsers().stream()
                        .filter(AbstractBaseDto::isDeleteAction)
                        .map(GlobalPlatformMapper.INSTANCE::toEntity)
                        .map(GroupUserPK::new)
                        .toList();
            }

            if (!rolesToAdd.isEmpty()) {
                if (entity.getGroupRoles() == null) {
                    entity.setGroupRoles(new ArrayList<>(rolesToAdd.size()));
                }
                entity.getGroupRoles().addAll(rolesToAdd);
            }

            if (!usersToAdd.isEmpty()) {
                if (entity.getGroupUsers() == null) {
                    entity.setGroupUsers(new ArrayList<>(usersToAdd.size()));
                }
                entity.getGroupUsers().addAll(usersToAdd);
            }

            entity = dao.save(entity);

            if (!rolesToDelete.isEmpty()) {
                groupRoleDao.deleteAllById(rolesToDelete);
            }

            if (!usersToDelete.isEmpty()) {
                groupUserDao.deleteAllById(usersToDelete);
            }

            return GlobalPlatformMapper.INSTANCE.toDto(entity);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to update Group: " + e.getMessage(), e);
        }
    }

    @Override
    protected Function<GroupEntity, GroupDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected GroupDao getDao() {
        return dao;
    }
}
