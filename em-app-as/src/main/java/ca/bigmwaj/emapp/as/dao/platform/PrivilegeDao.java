package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.PrivilegeEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeDao extends AbstractDao<PrivilegeEntity, Short> {

    default Class<PrivilegeEntity> getEntityClass() {
        return PrivilegeEntity.class;
    }
}
