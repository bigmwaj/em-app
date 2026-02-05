package ca.bigmwaj.emapp.as.dao.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByItem;
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

    public static void appendFilter(QueryConfig.QueryConfigBuilder qb, FilterItem filterItem) {
        var dbFieldName = filterItem.getName();
        var v = filterItem.getValues();
        var rootEntity = Q_ROOT;

        if (filterItem.getEntityFieldName() != null && !filterItem.getEntityFieldName().isEmpty()) {
            dbFieldName = filterItem.getEntityFieldName();
        }

        if (filterItem.getRootEntityName() != null && !filterItem.getRootEntityName().isEmpty()) {
            rootEntity = filterItem.getRootEntityName();
        }

        var q = switch (filterItem.getOper()) {
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

    public static void appendSortBy(QueryConfig.QueryConfigBuilder qb, SortByItem sortByItem) {
        var dbFieldName = sortByItem.getName();
        var rootEntity = Q_ROOT;

        if (sortByItem.getEntityFieldName() != null && !sortByItem.getEntityFieldName().isEmpty()) {
            dbFieldName = sortByItem.getEntityFieldName();
        }
        if (sortByItem.getRootEntityName() != null && !sortByItem.getRootEntityName().isEmpty()) {
            rootEntity = sortByItem.getRootEntityName();
        }

        SortByItem.sortType sortType = sortByItem.getSortType();
        if( sortType == null ){
            sortType = SortByItem.sortType.asc;
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