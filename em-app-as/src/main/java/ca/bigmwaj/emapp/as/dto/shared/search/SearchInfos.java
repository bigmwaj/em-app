package ca.bigmwaj.emapp.as.dto.shared.search;

import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class SearchInfos {

    public static final Short DEFAULT_QUERY_LIMIT = 1_000;

    private Long total;

    private Short pageSize;

    private Integer pageIndex;

    private boolean calculateStatTotal;

    public Integer getOffset() {
        if (pageSize != null) {
            return pageSize * pageIndex;
        }
        return pageIndex;
    }

    public Short getLimit() {
        if (pageSize == null) {
            return DEFAULT_QUERY_LIMIT;
        }
        return pageSize;
    }

    public SearchInfos(AbstractSearchCriteria searchCriteria) {
        setCalculateStatTotal(searchCriteria.isCalculateStatTotal());
        setPageSize(searchCriteria.getPageSize());
        setPageIndex(searchCriteria.getPageIndex());
    }
}
