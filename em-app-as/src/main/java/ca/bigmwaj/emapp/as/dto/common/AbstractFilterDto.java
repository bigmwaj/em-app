package ca.bigmwaj.emapp.as.dto.common;

import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(setterPrefix = "with")
@Data
public abstract class AbstractFilterDto {

    public static final Short DEFAULT_QUERY_LIMIT = 1_000;

    private List<FilterItem> filterItems;

    private List<SortByItem> sortByItems;

    private Short pageSize;

    private Integer pageIndex;

    private boolean calculateStatTotal;

    @JsonIgnore
    public Integer getOffset() {
        if (pageSize != null) {
            return pageSize * pageIndex;
        }
        if( pageIndex == null ){
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
