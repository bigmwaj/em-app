package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dto.platform.AbstractContactPointDto;
import ca.bigmwaj.emapp.as.entity.platform.AbstractContactPointEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Objects;

public  abstract class AbstractContactPointService<E extends AbstractContactPointEntity, D extends AbstractContactPointDto>
        extends AbstractService {

    abstract JpaRepository<E, Long> getDao();

    void beforeCreate(E entity, D dto) {
        entity.setId(null);
        beforeCreateHistEntity(entity);
    }

    E beforeUpdate(E entity, D dto) {
        if (entity.getId() == null) {
            entity.setId(dto.getId());
        }
        Objects.requireNonNull(entity.getId(), "Email Entity ID must not be null for update");
        beforeCreateHistEntity(entity);

        return entity;
    }

    void delete(E entity, D dto) {
        if (entity.getId() == null) {
            entity.setId(dto.getId());
        }
        Objects.requireNonNull(entity.getId(), "Email Entity ID must not be null for deletion");
        getDao().delete(entity);
    }
}
