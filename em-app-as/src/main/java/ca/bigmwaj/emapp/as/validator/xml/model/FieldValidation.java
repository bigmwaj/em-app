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
    private String name;
    private List<ConditionConfig> conditions = new ArrayList<>();
}
