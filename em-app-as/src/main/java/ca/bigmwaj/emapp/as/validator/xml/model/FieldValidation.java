package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents field validation configuration from XML.
 * Example: <field name="username">
 */
@Data
public class FieldValidation {

    public enum fieldType{ field, dto, dtos};

    private fieldType type = fieldType.field;

    private String name;

    private List<ConditionConfig> conditions = new ArrayList<>();

    /**
     * This is used when the validation type is dto or dtos, to specify which validator to use for validating the fields of the dto or dtos.
      */
    private ValidationConfig validationConfig;
}
