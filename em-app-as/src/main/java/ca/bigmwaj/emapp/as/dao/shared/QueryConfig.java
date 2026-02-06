package ca.bigmwaj.emapp.as.dao.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.FilterBy;
import ca.bigmwaj.emapp.as.dto.shared.search.SortBy;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder(setterPrefix = "with")
public class QueryConfig {

    private String baseQuery;

    public final static String Q_ROOT = "qRoot";

    @Singular
    @Getter
    private Map<String, Object> params;

    @Singular
    private List<String> whereClauses;

    @Singular
    private List<String> sortByClauses;

    public static void appendFilter(QueryConfig.QueryConfigBuilder qb, FilterBy filterBy) {
        var dbFieldName = filterBy.getName();
        var v = filterBy.getValues();
        var rootEntity = Q_ROOT;

        if (filterBy.getEntityFieldName() != null && !filterBy.getEntityFieldName().isEmpty()) {
            dbFieldName = filterBy.getEntityFieldName();
        }

        if (filterBy.getRootEntityName() != null && !filterBy.getRootEntityName().isEmpty()) {
            rootEntity = filterBy.getRootEntityName();
        }

        var q = switch (filterBy.getOper()) {
            case like -> {
                var param = v.getFirst().toString().toLowerCase();
                qb.withParam(dbFieldName, "%" + param + "%");
                yield String.format("lower(%s.%s) like :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case eq, in -> {
                qb.withParam(dbFieldName, v);
                yield String.format("%s.%s in (:%s)", rootEntity, dbFieldName, dbFieldName);
            }
            case lt -> {
                qb.withParam(dbFieldName, v.getFirst());
                yield String.format("%s.%s < :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case lte -> {
                qb.withParam(dbFieldName, v.getFirst());
                yield String.format("%s.%s <= :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case gt -> {
                qb.withParam(dbFieldName, v.getFirst());
                yield String.format("%s.%s > :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case gte -> {
                qb.withParam(dbFieldName, v.getFirst());
                yield String.format("%s.%s >= :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case ne, ni -> {
                qb.withParam(dbFieldName, v);
                yield String.format("%s.%s not in (:%s)", rootEntity, dbFieldName, dbFieldName);
            }
            case btw -> {
                qb.withParam(dbFieldName + "Min", v.getFirst())
                        .withParam(dbFieldName + "Max", v.getLast());
                yield String.format("%s.%s between :%sMin and :%sMax", rootEntity, dbFieldName, dbFieldName, dbFieldName);
            }
        };
        qb.withWhereClause(q);
    }

    public static void appendSortBy(QueryConfig.QueryConfigBuilder qb, SortBy sortBy) {
        var dbFieldName = sortBy.getName();
        var rootEntity = Q_ROOT;

        if (sortBy.getEntityFieldName() != null && !sortBy.getEntityFieldName().isEmpty()) {
            dbFieldName = sortBy.getEntityFieldName();
        }
        if (sortBy.getRootEntityName() != null && !sortBy.getRootEntityName().isEmpty()) {
            rootEntity = sortBy.getRootEntityName();
        }

        SortBy.sortType sortType = sortBy.getType();
        if( sortType == null ){
            sortType = SortBy.sortType.asc;
        }

        qb.withSortByClause(String.format("%s.%s %s", rootEntity, dbFieldName, sortType));
    }

    public String getQueryString() {
        var query = baseQuery;
        if (whereClauses != null && !whereClauses.isEmpty()) {
            query += " where " + String.join(" and ", whereClauses);
        }
        if (sortByClauses != null && !sortByClauses.isEmpty()) {
            query += " order by " + String.join(", ", sortByClauses);
        }

        return query;
    }
}