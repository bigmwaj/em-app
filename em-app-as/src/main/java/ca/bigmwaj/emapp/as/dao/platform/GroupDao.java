package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.GroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupDao extends AbstractDao<GroupEntity, Short> {

    default Class<GroupEntity> getEntityClass() {
        return GroupEntity.class;
    }

    boolean existsByNameIgnoreCase(String string);
}
