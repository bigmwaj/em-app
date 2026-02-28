package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.*;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleUserDto;
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
public class RoleService extends AbstractMainService<RoleDto, RoleEntity, Short> {

    @Autowired
    private RoleDao dao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private RolePrivilegeDao rolePrivilegeDao;

    @Override
    protected Function<RoleEntity, RoleDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected RoleDao getDao() {
        return dao;
    }

    public SearchResultDto<RolePrivilegeDto> findRolePrivileges(Short roleId) {
        Objects.requireNonNull(roleId, "Role ID cannot be null for finding role privileges.");
        List<RolePrivilegeDto> result = rolePrivilegeDao.findByRoleId(roleId).stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();
        return new SearchResultDto<>(result);
    }

    public SearchResultDto<RoleUserDto> findRoleUsers(Short roleId) {
        Objects.requireNonNull(roleId, "Role ID cannot be null for finding role users.");
        List<RoleUserDto> result = userRoleDao.findByRoleId(roleId).stream()
                .map(GlobalPlatformMapper.INSTANCE::toVirtualDto)
                .toList();
        return new SearchResultDto<>(result);
    }

    public void beforeDelete(Short roleId) {
        Objects.requireNonNull(roleId, "Role ID cannot be null for finding role privileges.");
        userRoleDao.deleteAll(userRoleDao.findByRoleId(roleId));
    }

    public RoleDto create(RoleDto dto) {
        try {
            RoleEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
            beforeCreateHistEntity(entity);

            // Add privileges to the role
            List<RolePrivilegeEntity> rolePrivileges = entity.getRolePrivileges();
            if (rolePrivileges != null && !rolePrivileges.isEmpty()) {
                for (RolePrivilegeEntity rolePrivilegeEntity : rolePrivileges) {
                    rolePrivilegeEntity.setRole(entity);
                    Short privilegeId = rolePrivilegeEntity.getPrivilege().getId();
                    PrivilegeEntity privilegeEntity = privilegeDao.findById(privilegeId)
                            .orElseThrow(() -> new NoSuchElementException("Privilege not found with id: " + privilegeId));
                    rolePrivilegeEntity.setPrivilege(privilegeEntity);
                    beforeCreateHistEntity(rolePrivilegeEntity);
                }
            }

            var createdRole = dao.save(entity);

            var roleUsers = dto.getRoleUsers();
            if (roleUsers != null && !roleUsers.isEmpty()) {
                for (RoleUserDto roleUserDto : roleUsers) {
                    var roleUserEntity = GlobalPlatformMapper.INSTANCE.toEntity(roleUserDto);
                    roleUserEntity.setRole(entity);
                    Short userId = roleUserEntity.getUser().getId();
                    var userEntity = userDao.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
                    roleUserEntity.setUser(userEntity);
                    beforeCreateHistEntity(roleUserEntity);
                    userRoleDao.save(roleUserEntity);
                }
            }
            return GlobalPlatformMapper.INSTANCE.toDto(createdRole);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to create Role: " + e.getMessage(), e);
        }
    }

    private RolePrivilegeEntity init(RolePrivilegeDto dto) {
        RolePrivilegeEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        Objects.requireNonNull(entity.getPrivilege(), "Privilege cannot be null.");

        Short privilegeId = entity.getPrivilege().getId();
        Objects.requireNonNull(privilegeId, "Privilege ID cannot be null.");

        PrivilegeEntity privilege = privilegeDao.findById(privilegeId)
                .orElseThrow(() -> new NoSuchElementException("Privilege not found with id: " + privilegeId));
        entity.setPrivilege(privilege);
        beforeCreateHistEntity(entity);
        return entity;
    }

    public RoleDto update(RoleDto dto) {
        try {
            Short roleId = dto.getId();
            Objects.requireNonNull(roleId, "Role ID cannot be null for update operation.");

            RoleEntity entity = dao.findById(roleId)
                    .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + roleId));

            entity.setDescription(dto.getDescription());
            beforeUpdateHistEntity(entity);

            List<RolePrivilegeEntity> toAdd = new ArrayList<>();
            List<RolePrivilegePK> toDelete = new ArrayList<>();

            if (dto.getRolePrivileges() != null && !dto.getRolePrivileges().isEmpty()) {
                toAdd = dto.getRolePrivileges().stream()
                        .filter(AbstractBaseDto::isCreateAction)
                        .map(this::init)
                        .toList();

                toDelete = dto.getRolePrivileges().stream()
                        .filter(AbstractBaseDto::isDeleteAction)
                        .map(GlobalPlatformMapper.INSTANCE::toEntity)
                        .map(RolePrivilegePK::new)
                        .toList();
            }

            if (!toAdd.isEmpty()) {
                if (entity.getRolePrivileges() == null) {
                    entity.setRolePrivileges(new ArrayList<>(toAdd.size()));
                }
                entity.getRolePrivileges().addAll(toAdd);
            }

            entity = dao.save(entity);

            if (!toDelete.isEmpty()) {
                rolePrivilegeDao.deleteAllById(toDelete);
            }
            List<RoleUserDto> roleUsers = dto.getRoleUsers();

            if (roleUsers != null && !roleUsers.isEmpty()) {
                for (RoleUserDto roleUserDto : roleUsers) {
                    UserRoleEntity userRoleEntity = GlobalPlatformMapper.INSTANCE.toEntity(roleUserDto);
                    switch (roleUserDto.getEditAction()) {
                        case CREATE:
                            Short userId = userRoleEntity.getUser().getId();
                            Objects.requireNonNull(userId, "User ID cannot be null for update operation.");

                            UserEntity userEntity = userDao.findById(userId)
                                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
                            userRoleEntity.setUser(userEntity);
                            userRoleEntity.setRole(entity);
                            beforeCreateHistEntity(userRoleEntity);
                            userRoleDao.save(userRoleEntity);
                            break;

                        case DELETE:
                            UserRolePK pk = new UserRolePK(userRoleEntity);
                            userRoleDao.deleteById(pk);
                    }
                }
            }

            return GlobalPlatformMapper.INSTANCE.toDto(entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to update Role: " + e.getMessage(), e);
        }
    }
}
