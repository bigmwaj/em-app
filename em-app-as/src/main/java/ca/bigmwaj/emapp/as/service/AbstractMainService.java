package ca.bigmwaj.emapp.as.service;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractMainService<D extends AbstractBaseDto, E extends AbstractBaseEntity, ID> extends AbstractBaseService<D, E> {

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

    public D findById(ID id) {
        Objects.requireNonNull(id, "ID cannot be null for findById.");
        return getDao().findById(id)
                .map(getEntityToDtoMapper())
                .orElseThrow(() -> new NoSuchElementException("Entity not found with id: " + id));
    }

    protected void beforeDelete(ID id){

    }

    protected void afterDelete(ID id){}

    public void deleteById(ID id) {
        Objects.requireNonNull(id, "ID cannot be null for deleteById.");
        beforeDelete(id);
        getDao().deleteById(id);
        afterDelete(id);
    }
}
