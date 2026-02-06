package ca.bigmwaj.emapp.as.api.shared.search;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder(setterPrefix = "with")
@Data
public abstract class AbstractQueryInput {

    private String name;

    private String entityFieldName;

    private String rootEntityName;

    private final List<String> validationErrorMessages = new ArrayList<>();

    public void addMessage(String message) {
        validationErrorMessages.add(message);
    }

}
