package ca.bigmwaj.emapp.as.dto.shared.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FilterBy extends AbstractClauseBy{

    public enum oper {
        eq,
        ne,
        in,
        ni,
        btw,
        lt,
        lte,
        gt,
        gte,
        like
    }

    private oper oper;

    private List<?> values;

    public FilterBy(String name){
        super(name);
    }

    public FilterBy(String name, oper oper){
        this(name);
        this.oper = oper;
    }

    public FilterBy(String name, oper oper, List<?> values){
        this(name, oper);
        this.values = values;
    }

    public void transformValues(Function<String, ?> operator) {
        if (values != null) {
            values = values
                    .stream()
                    .map(String.class::cast)
                    .map(String::trim)
                    .map(operator)
                    .toList();
        }
    }
}
