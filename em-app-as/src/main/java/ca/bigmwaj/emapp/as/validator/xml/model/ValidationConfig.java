package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root validation configuration from XML.
 */
@Data
public class ValidationConfig {
    private List<ValidationEntry> entries = new ArrayList<>();
}
