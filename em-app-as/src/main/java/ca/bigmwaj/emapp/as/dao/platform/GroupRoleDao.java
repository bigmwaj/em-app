package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.GroupRoleEntity;
import ca.bigmwaj.emapp.as.entity.platform.GroupRolePK;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRoleDao extends AbstractDao<GroupRoleEntity, GroupRolePK> {

    default Class<GroupRoleEntity> getEntityClass() {
        return GroupRoleEntity.class;
    }
}
