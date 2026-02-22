package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.PrivilegeDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeDto;
import ca.bigmwaj.emapp.as.entity.platform.PrivilegeEntity;
import ca.bigmwaj.emapp.as.lvo.platform.PrivilegeLvo;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class PrivilegeService extends AbstractMainService<PrivilegeDto, PrivilegeEntity, Short> {

    @Autowired
    private PrivilegeDao dao;

    @Override
    protected Function<PrivilegeEntity, PrivilegeDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected PrivilegeDao getDao() {
        return dao;
    }

    public long syncPrivileges(){
        var existing = dao.findAll().stream().map(PrivilegeEntity::getName).toList();
        Predicate<String> isExisting = existing::contains;
        var entities = Arrays.stream(PrivilegeLvo.values())
                .map(PrivilegeLvo::name)
                .filter(isExisting.negate())
                .map(this::map)
                .toList();
        dao.saveAll(entities);
        return entities.size();
    }

    private PrivilegeEntity map(String name) {
        var entity = new PrivilegeEntity();
        entity.setName(name);
        entity.setDescription(PrivilegeLvo.valueOf(name).getDescription());
        beforeCreateHistEntity(entity);
        return entity;
    }
}
