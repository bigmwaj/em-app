package ca.bigmwaj.emapp.as.dto.shared.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class FilterItem {

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

    private final List<String> validationErrorMessages = new ArrayList<>();

    private String name;

    private String entityFieldName;

    private String rootEntityName;

    private oper oper;

    private List<?> values;

    public void addMessage(String message) {
        validationErrorMessages.add(message);
    }

    public boolean isNotValid() {
        return !validationErrorMessages.isEmpty();
    }

    public boolean isValid() {
        return validationErrorMessages.isEmpty();
    }

    public void addMessages(List<String> messages) {
        validationErrorMessages.addAll(messages);
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
