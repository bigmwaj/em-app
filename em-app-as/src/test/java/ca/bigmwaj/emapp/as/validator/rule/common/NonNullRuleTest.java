package ca.bigmwaj.emapp.as.validator.rule.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class NonNullRuleTest extends AbstractRuleTest {

    @BeforeEach
    public void beforeEach() {
        rule = new NonNullRule();
    }

    @Test
    void test_given_nullValue_whenIsValid_then_return_false() {
        boolean isValid = rule.isValid(null, null);
        assertFalse(isValid);
    }

    @Test
    void test_given_nonNullValue_whenIsValid_then_return_true() {
        boolean isValid = rule.isValid("test", null);
        assertTrue(isValid);
    }
}