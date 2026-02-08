package ca.bigmwaj.emapp.as.dao;

import ca.bigmwaj.emapp.as.dao.shared.QueryConfig;
import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface AbstractDao<E extends AbstractBaseEntity, ID> extends JpaRepository<E, ID> {

    Class<E> getEntityClass();

    default QueryConfig prepareQueryConfig(QueryConfig.QueryConfigBuilder builder, AbstractSearchCriteria searchCriteria) {
        if( searchCriteria.getFilterByItems() != null && !searchCriteria.getFilterByItems().isEmpty() ){
            searchCriteria.getFilterByItems().forEach(e -> QueryConfig.appendFilter(builder, e));
        }

        if( searchCriteria.getSortByItems() != null && !searchCriteria.getSortByItems().isEmpty() ){
            searchCriteria.getSortByItems().forEach(e -> QueryConfig.appendSortBy(builder, e));
        }
        return builder.build();
    }

    default <T> TypedQuery<T> prepareQuery(EntityManager em, Class<T> klass, QueryConfig.QueryConfigBuilder builder, AbstractSearchCriteria sc) {
        var queryConfig = prepareQueryConfig(builder, sc);

        TypedQuery<T> query = em.createQuery(queryConfig.getQueryString(), klass);

        var params = queryConfig.getParams();
        queryConfig.getParams().keySet().forEach(k -> query.setParameter(k, params.get(k)));

        return query;
    }

    default String getQuery(String queryPart){
        return String.format("select %s from %s %s", queryPart, getEntityClass().getSimpleName(), QueryConfig.Q_ROOT);
    }

    default String getFindAllQuery(){
        return getQuery(QueryConfig.Q_ROOT);
    }

    default List<E> findAllByCriteria(EntityManager em, AbstractSearchCriteria sc) {
        var builder = QueryConfig.builder().withBaseQuery(getFindAllQuery());
        return prepareQuery(em, getEntityClass(), builder, sc)
                .setFirstResult(sc.getOffset())
                .setMaxResults(sc.getLimit())
                .getResultList();
    }

    default String getCountAllQuery(){
        return getQuery(String.format("count(%s)", QueryConfig.Q_ROOT));
    }

    default Long countAllByCriteria(EntityManager em, AbstractSearchCriteria sc) {
        var builder = QueryConfig.builder().withBaseQuery(getCountAllQuery());
        return prepareQuery(em, Long.class, builder, sc).getSingleResult();
    }
}