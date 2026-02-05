package ca.bigmwaj.emapp.as.api.search;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder(setterPrefix = "with")
@Data
public class FilterItemInput {

    private String name;

    private String entityFieldName;

    private String rootEntityName;

    private String oper;

    private String values;

    private final List<String> validationErrorMessages = new ArrayList<>();

    public void addMessage(String message) {
        validationErrorMessages.add(message);
    }

}
