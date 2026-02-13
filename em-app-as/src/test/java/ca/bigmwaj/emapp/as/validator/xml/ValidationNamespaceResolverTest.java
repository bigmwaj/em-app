package ca.bigmwaj.emapp.as.validator.xml;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidationNamespaceResolverTest {

    @Autowired
    private ValidationNamespaceResolver resolver;

    @Test
    void testResolveNamespace_ValidPlatformAccount() {
        InputStream stream = resolver.resolveNamespace("platform/account");
        assertNotNull(stream);
    }

    @Test
    void testResolveNamespace_InvalidNamespace() {
        assertThrows(ValidationConfigurationException.class, () -> {
            resolver.resolveNamespace("nonexistent/test");
        });
    }

    @Test
    void testResolveNamespace_NullNamespace() {
        assertThrows(ValidationConfigurationException.class, () -> {
            resolver.resolveNamespace(null);
        });
    }

    @Test
    void testResolveNamespace_EmptyNamespace() {
        assertThrows(ValidationConfigurationException.class, () -> {
            resolver.resolveNamespace("");
        });
    }
}
