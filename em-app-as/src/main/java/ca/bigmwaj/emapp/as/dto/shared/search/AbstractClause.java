package ca.bigmwaj.emapp.as.dto.shared.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public abstract class AbstractClause {

    @JsonIgnore
    private final List<String> validationErrorMessages = new ArrayList<>();
    private String name;
    @JsonIgnore
    private String entityFieldName;
    @JsonIgnore
    private String rootEntityName;

    public AbstractClause(String name) {
        this.name = name;
    }

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
}
