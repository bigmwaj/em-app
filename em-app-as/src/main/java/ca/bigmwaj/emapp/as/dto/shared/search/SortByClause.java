package ca.bigmwaj.emapp.as.dto.shared.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SortByClause extends AbstractClause {

    private sortType type;

    public SortByClause(String name) {
        super(name);
    }

    public SortByClause(String name, sortType type) {
        this(name);
        this.type = type;
    }

    public enum sortType {
        asc, desc
    }
}
