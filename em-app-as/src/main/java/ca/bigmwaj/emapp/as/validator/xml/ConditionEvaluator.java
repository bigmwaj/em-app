package ca.bigmwaj.emapp.as.validator.xml;

import org.jspecify.annotations.NonNull;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static ca.bigmwaj.emapp.as.validator.shared.ValidDto.*;

@Component
public class ConditionEvaluator {

    private static @NonNull StandardEvaluationContext getStandardEvaluationContext(String operation, Object dto) {
        StandardEvaluationContext context = new StandardEvaluationContext(dto);
        context.setVariable("create", CREATE.equals(operation));
        context.setVariable("update", UPDATE.equals(operation));
        context.setVariable("delete", DELETE.equals(operation));
        context.setVariable("changeStatus", CHANGE_STATUS.equals(operation));

        context.setVariable("createOrUpdate", List.of(CREATE, UPDATE).contains(operation));
        context.setVariable("updateOrChangeStatus", List.of(UPDATE, CHANGE_STATUS).contains(operation));
        context.setVariable("createUpdateOrChangeStatus", List.of(CREATE, UPDATE, CHANGE_STATUS).contains(operation));
        context.setVariable("createOrChangeStatus", List.of(CREATE, CHANGE_STATUS).contains(operation));
        return context;
    }

    public boolean evaluate(String operation, String expression, Object dto) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }

        try {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = getStandardEvaluationContext(operation, dto);
            Expression exp = parser.parseExpression(expression);
            return Boolean.TRUE.equals(exp.getValue(context, Boolean.class));
        } catch (ParseException e) {
            throw new ValidationConfigurationException("Failed to parse condition expression: " + expression, e);
        } catch (EvaluationException e) {
            throw new ValidationConfigurationException("Failed to evaluate condition expression: " + expression, e);
        }
    }
}
