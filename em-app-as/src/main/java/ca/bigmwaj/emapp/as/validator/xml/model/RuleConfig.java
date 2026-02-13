package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a validation rule in the XML configuration.
 * Example: <rule type="NonNullRule" />
 * Example: <rule type="MaxLengthRule" maxLength="32" />
 */
@Data
public class RuleConfig {
    private String type;
    private Map<String, String> parameters = new HashMap<>();
}
