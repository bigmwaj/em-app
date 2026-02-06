package ca.bigmwaj.emapp.as.dto.shared.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SortBy extends AbstractClauseBy{

    public enum sortType{
        asc, desc
    }

    public SortBy(String name){
        super(name);
    }

    public SortBy(String name, sortType type){
        this(name);
        this.type = type;
    }

    private sortType type;
}
