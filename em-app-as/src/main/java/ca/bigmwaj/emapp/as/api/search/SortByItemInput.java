package ca.bigmwaj.emapp.as.api.search;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder(setterPrefix = "with")
@Data
public class SortByItemInput {

    private String name;

    private String sortType;

    private String entityFieldName;

    private String rootEntityName;

    private final List<String> validationErrorMessages = new ArrayList<>();

    public void addMessage(String message) {
        validationErrorMessages.add(message);
    }

    public boolean isNotValid() {
        return !validationErrorMessages.isEmpty();
    }
}
