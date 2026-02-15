package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegeEntity;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegePK;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePrivilegeDao extends AbstractDao<RolePrivilegeEntity, RolePrivilegePK> {

    default Class<RolePrivilegeEntity> getEntityClass() {
        return RolePrivilegeEntity.class;
    }
}
