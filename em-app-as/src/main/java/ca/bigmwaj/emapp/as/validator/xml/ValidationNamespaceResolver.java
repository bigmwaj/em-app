package ca.bigmwaj.emapp.as.validator.xml;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Resolves validation namespace to XML file path.
 * Example: "platform/account" → /validator/platform.xml
 */
@Component
public class ValidationNamespaceResolver {

    private static final String VALIDATOR_BASE_PATH = "validator/";
    private static final String XML_EXTENSION = ".xml";

    /**
     * Resolves namespace to XML file input stream.
     * 
     * @param namespace The namespace (e.g., "platform/account")
     * @return InputStream of the XML file
     * @throws ValidationConfigurationException if file not found
     */
    public InputStream resolveNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            throw new ValidationConfigurationException("Namespace cannot be null or empty");
        }

        var resourcePath = VALIDATOR_BASE_PATH + namespace + XML_EXTENSION;

        try {
            Resource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                throw new ValidationConfigurationException(
                    "Validation configuration file not found: " + resourcePath + " for namespace: " + namespace
                );
            }
            return resource.getInputStream();
        } catch (Exception e) {
            throw new ValidationConfigurationException(
                "Failed to load validation configuration for namespace: " + namespace, e
            );
        }
    }
}
