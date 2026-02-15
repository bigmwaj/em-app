package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.UserRoleEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserRolePK;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleDao extends AbstractDao<UserRoleEntity, UserRolePK> {

    default Class<UserRoleEntity> getEntityClass() {
        return UserRoleEntity.class;
    }
}
