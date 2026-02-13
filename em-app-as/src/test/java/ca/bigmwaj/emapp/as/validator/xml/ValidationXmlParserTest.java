package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.xml.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidationXmlParserTest {

    @Autowired
    private ValidationXmlParser parser;

    @Test
    void testParsePlatformXml() throws Exception {
        InputStream stream = new ClassPathResource("validator/platform.xml").getInputStream();
        ValidationConfig config = parser.parse(stream);

        assertNotNull(config);
        assertFalse(config.getEntries().isEmpty());

        // Find account entry
        ValidationEntry accountEntry = config.getEntries().stream()
            .filter(e -> "account".equals(e.getName()))
            .findFirst()
            .orElse(null);

        assertNotNull(accountEntry);
        assertFalse(accountEntry.getFields().isEmpty());

        // Verify field validations exist
        assertTrue(accountEntry.getFields().stream()
            .anyMatch(f -> "id".equals(f.getName())));
        assertTrue(accountEntry.getFields().stream()
            .anyMatch(f -> "name".equals(f.getName())));
        assertTrue(accountEntry.getFields().stream()
            .anyMatch(f -> "status".equals(f.getName())));
    }

    @Test
    void testParseXmlWithConditionsAndRules() throws Exception {
        InputStream stream = new ClassPathResource("validator/test.xml").getInputStream();
        ValidationConfig config = parser.parse(stream);

        assertNotNull(config);
        ValidationEntry entry = config.getEntries().get(0);
        assertNotNull(entry);

        FieldValidation field = entry.getFields().get(0);
        assertNotNull(field);

        assertFalse(field.getConditions().isEmpty());
        ConditionConfig condition = field.getConditions().get(0);
        assertNotNull(condition.getExpression());

        assertFalse(condition.getRules().isEmpty());
        RuleConfig rule = condition.getRules().get(0);
        assertNotNull(rule.getType());
    }
}
