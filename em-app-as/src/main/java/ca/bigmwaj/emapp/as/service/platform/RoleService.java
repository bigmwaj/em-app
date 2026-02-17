package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.RoleDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.service.AbstractService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class RoleService extends AbstractService {

    @Autowired
    private RoleDao dao;

    @PersistenceContext
    private EntityManager entityManager;

    protected SearchResultDto<RoleDto> searchAll() {
        var r = dao.findAll().stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();
        return new SearchResultDto<>(r);
    }

    public SearchResultDto<RoleDto> search(DefaultSearchCriteria sc) {
        if (sc == null) {
            return searchAll();
        }

        var searchStats = new SearchInfos(sc);

        if (sc.isCalculateStatTotal()) {
            var total = dao.countAllByCriteria(entityManager, sc);
            searchStats.setTotal(total);
        }
        var r = dao.findAllByCriteria(entityManager, sc)
                .stream()
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .toList();

        return new SearchResultDto<>(searchStats, r);
    }

    public RoleDto findById(Short roleId) {
        return dao.findById(roleId)
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + roleId));
    }

    public void deleteById(Short roleId) {
        dao.deleteById(roleId);
    }

    public RoleDto create(RoleDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        entity = dao.save(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
    }

    public RoleDto update(RoleDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        entity = dao.save(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
    }
}
