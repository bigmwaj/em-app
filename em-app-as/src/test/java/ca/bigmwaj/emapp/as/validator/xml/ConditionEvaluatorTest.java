package ca.bigmwaj.emapp.as.validator.xml;

import static ca.bigmwaj.emapp.as.validator.shared.ValidDto.*;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ConditionEvaluatorTest {

    @Autowired
    private ConditionEvaluator evaluator;

    @Data
    static class TestDto {
        private EditActionLvo editAction;
        private String name;
        private Long id;
    }

    @Test
    void testEvaluate_ContextVariable() {
        TestDto dto = new TestDto();
        boolean result = evaluator.evaluate(CREATE, "#createOperation", dto);
        assertTrue(result);
    }

    @Test
    void testEvaluate_TrueLiteral() {
        TestDto dto = new TestDto();
        boolean result = evaluator.evaluate(CREATE, "true", dto);
        assertTrue(result);
    }

    @Test
    void testEvaluate_FalseLiteral() {
        TestDto dto = new TestDto();
        boolean result = evaluator.evaluate(CREATE, "false", dto);
        assertFalse(result);
    }

    @Test
    void testEvaluate_EqualityMatch() {
        TestDto dto = new TestDto();
        dto.setEditAction(EditActionLvo.CREATE);

        boolean result = evaluator.evaluate(CREATE, "editAction.toString() == 'CREATE'", dto);
        assertTrue(result);
    }

    @Test
    void testEvaluate_EqualityNoMatch() {
        TestDto dto = new TestDto();
        dto.setEditAction(EditActionLvo.UPDATE);

        boolean result = evaluator.evaluate(CREATE, "editAction == 'CREATE'", dto);
        assertFalse(result);
    }

    @Test
    void testEvaluate_InequalityMatch() {
        TestDto dto = new TestDto();
        dto.setEditAction(EditActionLvo.UPDATE);

        boolean result = evaluator.evaluate(CREATE, "editAction != 'CREATE'", dto);
        assertTrue(result);
    }

    @Test
    void testEvaluate_InequalityNoMatch() {
        TestDto dto = new TestDto();
        dto.setEditAction(EditActionLvo.CREATE);

        boolean result = evaluator.evaluate(CREATE, "editAction.toString() != 'CREATE'", dto);
        assertFalse(result);
    }

    @Test
    void testEvaluate_NullValue() {
        TestDto dto = new TestDto();
        dto.setName(null);

        boolean result = evaluator.evaluate(CREATE, "name == null", dto);
        assertTrue(result);
    }

    @Test
    void testEvaluate_EmptyExpression() {
        TestDto dto = new TestDto();
        boolean result = evaluator.evaluate(CREATE, "", dto);
        assertFalse(result);
    }

    @Test
    void testEvaluate_InvalidExpression() {
        TestDto dto = new TestDto();
        assertThrows(ValidationConfigurationException.class, () -> {
            evaluator.evaluate(CREATE, "invalid expression", dto);
        });
    }
}
