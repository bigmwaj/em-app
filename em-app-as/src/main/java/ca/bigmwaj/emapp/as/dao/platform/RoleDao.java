package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends AbstractDao<RoleEntity, Short> {

    default Class<RoleEntity> getEntityClass() {
        return RoleEntity.class;
    }

    boolean existsByNameIgnoreCase(String name);
}
