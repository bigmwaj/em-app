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

    private static final String ACCOUNT_XML_PATH = "validator/platform/account.xml";
    private static final String TEST_XML_PATH = "validator/test.xml";

    @Autowired
    private ValidationXmlParser parser;

    @Test
    void testParsePlatformAccountXml() throws Exception {
        var stream = new ClassPathResource(ACCOUNT_XML_PATH).getInputStream();
        var config = parser.parse(stream);

        assertNotNull(config);
        assertFalse(config.getFields().isEmpty());

        // Verify field validations exist
        assertTrue(config.getFields().stream()
            .anyMatch(f -> "id".equals(f.getName())));
        assertTrue(config.getFields().stream()
            .anyMatch(f -> "name".equals(f.getName())));
        assertTrue(config.getFields().stream()
            .anyMatch(f -> "status".equals(f.getName())));
    }

    @Test
    void testParseXmlWithConditionsAndRules() throws Exception {
        InputStream stream = new ClassPathResource(TEST_XML_PATH).getInputStream();
        ValidationConfig config = parser.parse(stream);

        assertNotNull(config);

        FieldValidation field = config.getFields().get(0);
        assertNotNull(field);

        assertFalse(field.getConditions().isEmpty());
        ConditionConfig condition = field.getConditions().get(0);
        assertNotNull(condition.getExpression());

        assertFalse(condition.getRules().isEmpty());
        RuleConfig rule = condition.getRules().get(0);
        assertNotNull(rule.getType());
    }
}
