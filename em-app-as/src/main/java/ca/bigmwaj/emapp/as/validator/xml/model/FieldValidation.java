package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents field validation configuration from XML.
 * Example: <field name="username">
 */
@Data
public class FieldValidation {

    private fieldType type = fieldType.field;

    private String name;

    private String path;

    private List<ConditionConfig> conditions = new ArrayList<>();

    /**
     * This is used when the validation type is dto or dtoList, to specify which validator to use for validating the fields of the dto or dtos.
     */
    private ValidationConfig validationConfig;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FieldValidation that = (FieldValidation) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }

    public enum fieldType {field, dto, dtoList}
}
