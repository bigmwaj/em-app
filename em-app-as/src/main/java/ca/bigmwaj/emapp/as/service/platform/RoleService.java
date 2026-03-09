package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.*;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.platform.RoleEntity;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegeEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserRoleEntity;
import ca.bigmwaj.emapp.as.mapper.RoleMapper;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import ca.bigmwaj.emapp.as.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class RoleService extends AbstractMainService<RoleDto, RoleEntity, Short> {

    private final RoleDao dao;

    private final RoleMapper mapper;

    private final UserRoleDao userRoleDao;

    private final RolePrivilegeDao rolePrivilegeDao;

    @Autowired
    public RoleService(RoleDao dao, RoleMapper mapper, UserRoleDao userRoleDao, RolePrivilegeDao rolePrivilegeDao) {
        this.dao = dao;
        this.mapper = mapper;
        this.userRoleDao = userRoleDao;
        this.rolePrivilegeDao = rolePrivilegeDao;
    }

    @Override
    protected Function<RoleEntity, RoleDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected RoleDao getDao() {
        return dao;
    }

    public DataListDto<RolePrivilegeDto> findRolePrivileges(Short roleId, DefaultSearchCriteria sc) {
        Objects.requireNonNull(roleId, "Role ID cannot be null for finding role privileges.");
        Example<RolePrivilegeEntity> example = Example.of(new RolePrivilegeEntity());
        example.getProbe().setRole(new RoleEntity());
        example.getProbe().getRole().setId(roleId);

        Pageable pageable = PageRequest.of(sc.getPageIndex(), sc.getLimit());

        Long total = rolePrivilegeDao.count(example);

        SearchInfos searchInfos = new SearchInfos(sc);
        searchInfos.setTotal(total);

        List<RolePrivilegeDto> result = rolePrivilegeDao.findAll(example, pageable).stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();

        return new DataListDto<>(searchInfos, result);
    }

    public DataListDto<RoleUserDto> findRoleUsers(Short roleId, DefaultSearchCriteria sc) {
        Objects.requireNonNull(roleId, "Role ID cannot be null for finding role users.");

        Example<UserRoleEntity> example = Example.of(new UserRoleEntity());
        example.getProbe().setRole(new RoleEntity());
        example.getProbe().getRole().setId(roleId);

        Pageable pageable = PageRequest.of(sc.getPageIndex(), sc.getLimit());

        Long total = userRoleDao.count(example);
        SearchInfos searchInfos = new SearchInfos(sc);
        searchInfos.setTotal(total);

        List<RoleUserDto> result = userRoleDao.findAll(example, pageable).stream()
                .map(GlobalPlatformMapper.INSTANCE::toVirtualDto)
                .toList();


        return new DataListDto<>(searchInfos, result);
    }

    public void beforeDelete(Short roleId) {
        Objects.requireNonNull(roleId, "Role ID cannot be null for finding role privileges.");
        userRoleDao.deleteAll(userRoleDao.findByRoleId(roleId));
    }

    public RoleDto create(RoleDto dto) {
        try {
            final var entity = mapper.mappingForCreate(dto);

            if (dto.getRolePrivileges() != null && !dto.getRolePrivileges().isEmpty()) {
                var privileges = dto.getRolePrivileges().stream()
                        .map(child -> mapper.mappingForCreate(entity, child))
                        .toList();
                entity.setRolePrivileges(privileges);
            }

            var createdEntity = dao.save(entity);

            if (dto.getRoleUsers() != null && !dto.getRoleUsers().isEmpty()) {
                dto.getRoleUsers().stream()
                        .map(child -> mapper.mappingForCreate(entity, child))
                        .forEach(userRoleDao::save);
            }
            return GlobalPlatformMapper.INSTANCE.toDto(createdEntity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to create Role: " + e.getMessage(), e);
        }
    }

    private void removeDeletedPrivileges(RoleEntity entity, RoleDto dto) {
        Predicate<Short> deletable = id -> dto.getRolePrivileges().stream()
                .filter(RolePrivilegeDto::isDeleteAction)
                .map(RolePrivilegeDto::getPrivilege)
                .map(PrivilegeDto::getId)
                .anyMatch(id::equals);

        entity.getRolePrivileges().removeIf(id -> deletable.test(id.getPrivilege().getId()));
    }

    private void addNewPrivileges(RoleEntity entity, RoleDto dto) {
        dto.getRolePrivileges().stream()
                .filter(RolePrivilegeDto::isCreateAction)
                .map(e -> mapper.mappingForCreate(entity, e))
                .forEach(entity.getRolePrivileges()::add);
    }

    private void removeDeletedUsers(RoleEntity entity, RoleDto dto) {
        Predicate<Short> deletable = id -> dto.getRoleUsers().stream()
                .filter(RoleUserDto::isDeleteAction)
                .map(RoleUserDto::getUser)
                .map(UserDto::getId)
                .anyMatch(id::equals);

        userRoleDao.findByRoleId(entity.getId()).stream()
                .filter(ur -> deletable.test(ur.getUser().getId()))
                .forEach(userRoleDao::delete);
    }

    private void addNewUsers(RoleEntity entity, RoleDto dto) {
        dto.getRoleUsers().stream()
                .filter(RoleUserDto::isCreateAction)
                .map(e -> mapper.mappingForCreate(entity, e))
                .forEach(userRoleDao::save);
    }

    public RoleDto update(RoleDto dto) {
        try {
            var entity = mapper.mappingForUpdate(dto);

            if (dto.getRolePrivileges() != null && !dto.getRolePrivileges().isEmpty()) {
                removeDeletedPrivileges(entity, dto);
                addNewPrivileges(entity, dto);
            }

            if (dto.getRoleUsers() != null && !dto.getRoleUsers().isEmpty()) {
                removeDeletedUsers(entity, dto);
                addNewUsers(entity, dto);
            }

            return GlobalPlatformMapper.INSTANCE.toDto(entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to update Role: " + e.getMessage(), e);
        }
    }
}
