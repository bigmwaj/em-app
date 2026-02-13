package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class ConditionEvaluator {

    public boolean evaluate(String expression, Object dto) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }

        try {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext(dto);
            context.setVariable("Status", EditActionLvo.class);

            Expression exp = parser.parseExpression(expression);
            return Boolean.TRUE.equals(exp.getValue(context, Boolean.class));
        } catch (ParseException e) {
            throw new ValidationConfigurationException("Failed to parse condition expression: " + expression, e);
        } catch (EvaluationException e) {
            throw new ValidationConfigurationException("Failed to evaluate condition expression: " + expression, e);
        }
    }
}
