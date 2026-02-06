package ca.bigmwaj.emapp.as.dto.common;

import ca.bigmwaj.emapp.as.dto.shared.search.FilterBy;
import ca.bigmwaj.emapp.as.dto.shared.search.SortBy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(setterPrefix = "with")
@Data
public abstract class AbstractFilterDto {

    public static final Short DEFAULT_QUERY_LIMIT = 1_000;

    private List<FilterBy> filterBIES;

    private List<SortBy> sortBIES;

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
