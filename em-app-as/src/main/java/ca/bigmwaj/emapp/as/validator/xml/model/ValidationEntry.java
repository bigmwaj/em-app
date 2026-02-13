package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a validation entry point in the XML configuration.
 * Example: <entry name="account">
 */
@Data
public class ValidationEntry {
    private String name;
    private List<FieldValidation> fields = new ArrayList<>();
}
