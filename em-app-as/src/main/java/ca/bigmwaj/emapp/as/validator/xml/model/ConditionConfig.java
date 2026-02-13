package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a condition in the XML configuration.
 * Example: <condition expression="editAction == 'CREATE'">
 */
@Data
public class ConditionConfig {
    private String expression;
    private List<RuleConfig> rules = new ArrayList<>();
}
