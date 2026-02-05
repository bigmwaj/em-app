package ca.bigmwaj.emapp.as.dto.shared.search;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class SortByItem {

    public enum sortType{
        asc, desc
    }

    public SortByItem(String name){
        this.name = name;
    }

    public SortByItem(String name, sortType sortType){
        this(name);
        this.sortType = sortType;
    }

    private final List<String> validationErrorMessages = new ArrayList<>();

    private String name;

    private sortType sortType;

    private String entityFieldName;

    private String rootEntityName;

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
