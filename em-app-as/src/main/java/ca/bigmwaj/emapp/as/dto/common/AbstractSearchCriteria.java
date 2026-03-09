package ca.bigmwaj.emapp.as.dto.common;

import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public abstract class AbstractSearchCriteria {

    public static final Short DEFAULT_QUERY_LIMIT = 1_000;

    @Positive
    private Short pageSize;

    @PositiveOrZero
    private Integer pageIndex;

    private WhereClauseJoinOp whereClauseJoinOp;

    private boolean calculateStatTotal;

    @Valid
    private List<WhereClause> whereClauses;

    private String include;

    @Valid
    private List<SortByClause> sortByClauses;

    @JsonIgnore
    public Integer getOffset() {
        if (pageSize != null) {
            return pageSize * pageIndex;
        }
        if (pageIndex == null) {
            return 0;
        }
        return pageIndex;
    }

    @JsonIgnore
    public Short getLimit() {
        if (pageSize == null) {
            return DEFAULT_QUERY_LIMIT;
        }
        return pageSize;
    }
}
