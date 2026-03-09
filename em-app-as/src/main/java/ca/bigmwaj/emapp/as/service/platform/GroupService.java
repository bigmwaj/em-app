package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.GroupDao;
import ca.bigmwaj.emapp.as.dao.platform.GroupRoleDao;
import ca.bigmwaj.emapp.as.dao.platform.GroupUserDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.platform.*;
import ca.bigmwaj.emapp.as.mapper.GroupMapper;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import ca.bigmwaj.emapp.as.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class GroupService extends AbstractMainService<GroupDto, GroupEntity, Short> {

    private final GroupDao dao;

    private final GroupRoleDao groupRoleDao;

    private final GroupUserDao groupUserDao;

    private final GroupMapper mapper;

    @Autowired
    public GroupService(GroupDao dao, GroupRoleDao groupRoleDao, GroupUserDao groupUserDao, GroupMapper mapper) {
        this.dao = dao;
        this.groupRoleDao = groupRoleDao;
        this.groupUserDao = groupUserDao;
        this.mapper = mapper;
    }

    public DataListDto<GroupUserDto> findGroupUsers(Short groupId, DefaultSearchCriteria sc) {
        Objects.requireNonNull(groupId, "Role ID cannot be null for finding group users.");

        Example<GroupUserEntity> example = Example.of(new GroupUserEntity());
        example.getProbe().setGroup(new GroupEntity());
        example.getProbe().getGroup().setId(groupId);

        Pageable pageable = PageRequest.of(sc.getPageIndex(), sc.getLimit());

        Long total = groupUserDao.count(example);
        SearchInfos searchInfos = new SearchInfos(sc);
        searchInfos.setTotal(total);

        List<GroupUserDto> result = groupUserDao.findAll(example, pageable)
                .stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();
        return new DataListDto<>(searchInfos, result);
    }

    public DataListDto<GroupRoleDto> findGroupRoles(Short groupId, DefaultSearchCriteria sc) {

        Example<GroupRoleEntity> example = Example.of(new GroupRoleEntity());
        example.getProbe().setGroup(new GroupEntity());
        example.getProbe().getGroup().setId(groupId);

        Pageable pageable = PageRequest.of(sc.getPageIndex(), sc.getLimit());

        Long total = groupRoleDao.count(example);
        SearchInfos searchInfos = new SearchInfos(sc);
        searchInfos.setTotal(total);

        List<GroupRoleDto> result = groupRoleDao.findAll(example, pageable)
                .stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();
        return new DataListDto<>(searchInfos, result);
    }

    public GroupDto create(GroupDto dto) {
        try {
            final var entity = mapper.mappingForCreate(dto);

            if (dto.getGroupRoles() != null && !dto.getGroupRoles().isEmpty()) {
                var roles = dto.getGroupRoles().stream()
                        .map(child -> mapper.mappingForCreate(entity, child)).toList();
                entity.setGroupRoles(roles);
            }

            if (dto.getGroupUsers() != null && !dto.getGroupUsers().isEmpty()) {
                var users = dto.getGroupUsers().stream()
                        .map(child -> mapper.mappingForCreate(entity, child)).toList();
                entity.setGroupUsers(users);
            }

            return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("Failed to create Group: " + e.getMessage(), e);
        }
    }

    private void removeDeletedRoles(GroupEntity entity, GroupDto dto) {
        Predicate<Short> deletable = id -> dto.getGroupRoles().stream()
                .filter(GroupRoleDto::isDeleteAction)
                .map(GroupRoleDto::getRole)
                .map(RoleDto::getId)
                .anyMatch(id::equals);

        var finalList = entity.getGroupRoles().stream()
                .filter(Predicate.not(e -> deletable.test(e.getRole().getId())))
                .toList();

        entity.setGroupRoles(new ArrayList<>(finalList));
    }

    private void addNewRoles(GroupEntity entity, GroupDto dto) {
        dto.getGroupRoles().stream()
                .filter(GroupRoleDto::isCreateAction)
                .map(e -> mapper.mappingForCreate(entity, e))
                .forEach(entity.getGroupRoles()::add);
    }

    private void removeDeletedUsers(GroupEntity entity, GroupDto dto) {
        Predicate<Short> deletable = id -> dto.getGroupUsers().stream()
                .filter(GroupUserDto::isDeleteAction)
                .map(GroupUserDto::getUser)
                .map(UserDto::getId)
                .anyMatch(id::equals);

        var finalList = entity.getGroupUsers().stream()
                .filter(Predicate.not(e -> deletable.test(e.getUser().getId())))
                .toList();

        entity.setGroupUsers(new ArrayList<>(finalList));
    }

    private void addNewUsers(GroupEntity entity, GroupDto dto) {
        dto.getGroupUsers().stream()
                .filter(GroupUserDto::isCreateAction)
                .map(e -> mapper.mappingForCreate(entity, e))
                .forEach(entity.getGroupUsers()::add);
    }

    public GroupDto update(GroupDto dto) {
        try {
            var entity = mapper.mappingForUpdate(dto);

            if (dto.getGroupRoles() != null && !dto.getGroupRoles().isEmpty()) {
                if (entity.getGroupRoles() == null) {
                    entity.setGroupRoles(new ArrayList<>(dto.getGroupRoles().size()));
                }
                removeDeletedRoles(entity, dto);
                addNewRoles(entity, dto);
            }

            if (dto.getGroupUsers() != null && !dto.getGroupUsers().isEmpty()) {
                if (entity.getGroupUsers() == null) {
                    entity.setGroupUsers(new ArrayList<>(dto.getGroupUsers().size()));
                }
                removeDeletedUsers(entity, dto);
                addNewUsers(entity, dto);
            }

            return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));

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
