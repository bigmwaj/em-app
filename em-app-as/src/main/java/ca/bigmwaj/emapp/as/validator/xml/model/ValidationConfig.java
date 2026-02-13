package ca.bigmwaj.emapp.as.validator.xml.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root validation configuration from XML.
 */
@Data
public class ValidationConfig {
    private String ref;

    private List<FieldValidation> fields = new ArrayList<>();
}
