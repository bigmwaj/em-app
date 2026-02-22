package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.GroupDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.GroupDto;
import ca.bigmwaj.emapp.as.entity.platform.GroupEntity;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class GroupService extends AbstractMainService<GroupDto, GroupEntity, Short> {

    @Autowired
    private GroupDao dao;

    public GroupDto findById(Short groupId) {
        return dao.findById(groupId)
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException("Group not found with id: " + groupId));
    }

    public void deleteById(Short groupId) {
        dao.deleteById(groupId);
    }

    public GroupDto create(GroupDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        entity = dao.save(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
    }

    public GroupDto update(GroupDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        entity = dao.save(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
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
