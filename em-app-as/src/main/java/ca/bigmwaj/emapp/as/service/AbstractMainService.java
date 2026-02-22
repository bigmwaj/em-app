package ca.bigmwaj.emapp.as.service;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractMainService<D extends AbstractBaseDto, E extends AbstractBaseEntity, ID> extends AbstractBaseService<D, E>{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String SYSTEM_USER = "IA";

    @PersistenceContext
    private EntityManager entityManager;

    protected abstract Function<E, D> getEntityToDtoMapper();

    protected abstract AbstractDao<E, ID> getDao();

    public SearchResultDto<D> searchAll() {
        var r = getDao().findAll().stream()
                .map(this.getEntityToDtoMapper())
                .toList();
        return new SearchResultDto<>(r);
    }

    public SearchResultDto<D> search(DefaultSearchCriteria sc) {
        Objects.requireNonNull(sc);

        var searchStats = new SearchInfos(sc);

        if (sc.isCalculateStatTotal()) {
            var total = getDao().countAllByCriteria(entityManager, sc);
            searchStats.setTotal(total);
        }
        var r = getDao().findAllByCriteria(entityManager, sc)
                .stream()
                .map(this.getEntityToDtoMapper())
                .toList();

        return new SearchResultDto<>(searchStats, r);
    }
}
