package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.DeadLetterEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadLetterDao extends AbstractDao<DeadLetterEntity, Long> {

    default Class<DeadLetterEntity> getEntityClass() {
        return DeadLetterEntity.class;
    }

}
