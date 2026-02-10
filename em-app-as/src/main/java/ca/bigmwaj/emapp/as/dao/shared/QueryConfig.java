package ca.bigmwaj.emapp.as.dao.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Query configuration builder for constructing dynamic JPQL queries.
 * 
 * <p>This class provides a fluent API for building complex database queries with:
 * <ul>
 *   <li>Dynamic WHERE clauses based on filter criteria</li>
 *   <li>Parameterized queries to prevent SQL injection</li>
 *   <li>Support for multiple filter operators (like, eq, in, lt, lte, gt, gte, ne, ni, btw)</li>
 *   <li>Dynamic ORDER BY clauses</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * QueryConfig.QueryConfigBuilder qb = QueryConfig.builder()
 *     .withBaseQuery("SELECT qRoot FROM User qRoot");
 *     
 * // Add filter: firstName like '%john%'
 * WhereClause filter = WhereClause.builder()
 *     .name("firstName")
 *     .oper(FilterOperator.like)
 *     .values(List.of("john"))
 *     .build();
 * QueryConfig.appendFilter(qb, filter);
 * 
 * // Add sorting: ORDER BY lastName ASC
 * SortByClause sort = SortByClause.builder()
 *     .name("lastName")
 *     .direction("asc")
 *     .build();
 * QueryConfig.appendSort(qb, sort);
 * 
 * QueryConfig config = qb.build();
 * }</pre>
 * 
 * @see WhereClause
 * @see SortByClause
 */
@Builder(setterPrefix = "with")
public class QueryConfig {

    private WhereClauseJoinOp whereClauseJoinOp;

    /**
     * Base JPQL query string (e.g., "SELECT qRoot FROM User qRoot").
     */
    private String baseQuery;

    /**
     * Default root entity alias used in JPQL queries.
     */
    public final static String Q_ROOT = "qRoot";

    /**
     * Map of named parameters and their values for the query.
     * These parameters are referenced in the WHERE clause (e.g., :firstName).
     */
    @Singular
    @Getter
    private Map<String, Object> params;

    /**
     * List of WHERE clause fragments (e.g., "qRoot.firstName like :firstName").
     * These are combined with AND operators in the final query.
     */
    @Singular
    private List<String> whereClauses;

    /**
     * List of ORDER BY clause fragments (e.g., "qRoot.lastName asc").
     * These are combined in the final query.
     */
    @Singular
    private List<String> sortByClauses;

    /**
     * Appends a filter condition to the query configuration.
     * 
     * <p>Supports the following filter operators:
     * <ul>
     *   <li><b>like</b>: Case-insensitive pattern matching (e.g., firstName like '%john%')</li>
     *   <li><b>eq/in</b>: Equality or IN clause (e.g., status in ('ACTIVE', 'PENDING'))</li>
     *   <li><b>lt</b>: Less than (e.g., age &lt; 30)</li>
     *   <li><b>lte</b>: Less than or equal (e.g., age &lt;= 30)</li>
     *   <li><b>gt</b>: Greater than (e.g., age &gt; 18)</li>
     *   <li><b>gte</b>: Greater than or equal (e.g., age &gt;= 18)</li>
     *   <li><b>ne/ni</b>: Not equal or NOT IN clause (e.g., status not in ('DELETED'))</li>
     *   <li><b>btw</b>: Between (e.g., age between 18 and 65)</li>
     * </ul>
     * 
     * @param qb the query configuration builder to append to
     * @param whereClause the filter criteria containing field name, operator, and values
     */
    public static void appendWhereClause(QueryConfig.QueryConfigBuilder qb, WhereClause whereClause) {
        var dbFieldName = whereClause.getName();
        var v = whereClause.getValues();
        var rootEntity = Q_ROOT;

        // Allow custom field mapping (e.g., "user.firstName" instead of "firstName")
        if (whereClause.getEntityFieldName() != null && !whereClause.getEntityFieldName().isEmpty()) {
            dbFieldName = whereClause.getEntityFieldName();
        }

        // Allow custom entity alias (e.g., "u" instead of "qRoot")
        if (whereClause.getRootEntityName() != null && !whereClause.getRootEntityName().isEmpty()) {
            rootEntity = whereClause.getRootEntityName();
        }

        var q = switch (whereClause.getOper()) {
            case like -> {
                var param = v.get(0).toString().toLowerCase();
                qb.withParam(dbFieldName, "%" + param + "%");
                yield String.format("lower(%s.%s) like :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case eq, in -> {
                qb.withParam(dbFieldName, v);
                yield String.format("%s.%s in (:%s)", rootEntity, dbFieldName, dbFieldName);
            }
            case lt -> {
                qb.withParam(dbFieldName, v.get(0));
                yield String.format("%s.%s < :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case lte -> {
                qb.withParam(dbFieldName, v.get(0));
                yield String.format("%s.%s <= :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case gt -> {
                qb.withParam(dbFieldName, v.get(0));
                yield String.format("%s.%s > :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case gte -> {
                qb.withParam(dbFieldName, v.get(0));
                yield String.format("%s.%s >= :%s", rootEntity, dbFieldName, dbFieldName);
            }
            case ne, ni -> {
                qb.withParam(dbFieldName, v);
                yield String.format("%s.%s not in (:%s)", rootEntity, dbFieldName, dbFieldName);
            }
            case btw -> {
                qb.withParam(dbFieldName + "Min", v.get(0))
                        .withParam(dbFieldName + "Max", v.get(v.size() - 1));
                yield String.format("%s.%s between :%sMin and :%sMax", rootEntity, dbFieldName, dbFieldName, dbFieldName);
            }
        };
        qb.withWhereClause(q);
    }

    /**
     * Appends a sort-by clause to the query configuration.
     * 
     * <p>Creates an ORDER BY clause for the specified field with the given direction.
     * If no direction is specified, defaults to ascending (asc).
     * 
     * @param qb the query configuration builder to append to
     * @param sortBy the sort criteria containing field name and direction (asc/desc)
     */
    public static void appendSortByClause(QueryConfig.QueryConfigBuilder qb, SortByClause sortBy) {
        var dbFieldName = sortBy.getName();
        var rootEntity = Q_ROOT;

        // Allow custom field mapping
        if (sortBy.getEntityFieldName() != null && !sortBy.getEntityFieldName().isEmpty()) {
            dbFieldName = sortBy.getEntityFieldName();
        }
        
        // Allow custom entity alias
        if (sortBy.getRootEntityName() != null && !sortBy.getRootEntityName().isEmpty()) {
            rootEntity = sortBy.getRootEntityName();
        }

        // Default to ascending if no sort type specified
        SortByClause.sortType sortType = sortBy.getType();
        if( sortType == null ){
            sortType = SortByClause.sortType.asc;
        }

        qb.withSortByClause(String.format("%s.%s %s", rootEntity, dbFieldName, sortType));
    }

    /**
     * Builds the final JPQL query string with WHERE and ORDER BY clauses.
     * 
     * <p>Combines the base query with all accumulated WHERE clauses (joined with AND)
     * and ORDER BY clauses. Returns a complete JPQL query string ready for execution.
     * 
     * <h3>Example output:</h3>
     * <pre>
     * SELECT qRoot FROM User qRoot 
     * WHERE qRoot.firstName like :firstName AND qRoot.age >= :age 
     * ORDER BY qRoot.lastName asc
     * </pre>
     * 
     * @return the complete JPQL query string
     */
    public String getQueryString() {
        var query = baseQuery;
        if (whereClauses != null && !whereClauses.isEmpty()) {
            query += " where " + String.join(" " + whereClauseJoinOp + " ", whereClauses);
        }
        if (sortByClauses != null && !sortByClauses.isEmpty()) {
            query += " order by " + String.join(", ", sortByClauses);
        }

        return query;
    }
}