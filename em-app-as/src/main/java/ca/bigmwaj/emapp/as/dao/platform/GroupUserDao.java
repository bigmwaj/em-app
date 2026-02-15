package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserEntity;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserPK;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUserDao extends AbstractDao<GroupUserEntity, GroupUserPK> {

    default Class<GroupUserEntity> getEntityClass() {
        return GroupUserEntity.class;
    }
}
