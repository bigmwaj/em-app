package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.as.validator.ValidationException;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ConditionEvaluator;
import ca.bigmwaj.emapp.as.validator.xml.RuleFactory;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import ca.bigmwaj.emapp.as.validator.xml.ValidationXmlParser;
import ca.bigmwaj.emapp.as.validator.xml.model.ConditionConfig;
import ca.bigmwaj.emapp.as.validator.xml.model.FieldValidation;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import ca.bigmwaj.emapp.as.validator.xml.model.ValidationConfig;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ca.bigmwaj.emapp.as.validator.shared.ValidDto.CREATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringDtoValidatorTest {

    @Mock
    private ValidationXmlParser xmlParser;

    @Mock
    private RuleFactory ruleFactory;

    @Mock
    private ConditionEvaluator conditionEvaluator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private AbstractRule rule;

    @InjectMocks
    private SpringDtoValidator validator;

    private TestDto testDto;

    private ValidationConfig validationConfig;

    @BeforeEach
    void setUp() {
        testDto = new TestDto();
        testDto.setName("Test Name");

        // Initialize annotation
        ValidDto annotation = mock(ValidDto.class);
        when(annotation.value()).thenReturn("test.namespace");
        when(annotation.operation()).thenReturn(CREATE);

        validator.initialize(annotation);
    }

    @Test
    void isValid_withNullDto_shouldReturnTrue() {
        boolean result = validator.isValid(null, context);
        assertTrue(result);
    }

    @Test
    void isValid_withNonBaseDtoObject_shouldThrowValidationException() {
        Object notADto = "Not a DTO";

        assertThrows(ValidationException.class, () ->
            validator.isValid(notADto, context)
        );
    }

    @Test
    void isValid_withNoOperation_shouldThrowValidationException() {
        ValidDto annotation = mock(ValidDto.class);
        when(annotation.value()).thenReturn("test.namespace");
        when(annotation.operation()).thenReturn("");
        validator.initialize(annotation);

        assertThrows(ValidationException.class, () ->
            validator.isValid(testDto, context)
        );
    }

    @Test
    void isValid_withValidDto_shouldReturnTrue() throws Exception {
        setupValidValidationConfig();

        when(xmlParser.getValidationConfig(any(), eq("test.namespace")))
            .thenReturn(validationConfig);
        when(conditionEvaluator.evaluate(eq(CREATE), anyString(), any()))
            .thenReturn(true);
        when(ruleFactory.createRule(any(RuleConfig.class)))
            .thenReturn(rule);
        when(rule.validate(eq(context), any(), anyString(), anyMap()))
            .thenReturn(true);

        boolean result = validator.isValid(testDto, context);

        assertTrue(result);
        verify(xmlParser).getValidationConfig(any(), eq("test.namespace"));
    }

    @Test
    void isValid_withInvalidDto_shouldReturnFalse() throws Exception {
        setupValidValidationConfig();

        when(xmlParser.getValidationConfig(any(), eq("test.namespace")))
            .thenReturn(validationConfig);
        when(conditionEvaluator.evaluate(eq(CREATE), anyString(), any()))
            .thenReturn(true);
        when(ruleFactory.createRule(any(RuleConfig.class)))
            .thenReturn(rule);
        when(rule.validate(eq(context), any(), anyString(), anyMap()))
            .thenReturn(false);

        boolean result = validator.isValid(testDto, context);

        assertFalse(result);
    }

    @Test
    void isValid_withConditionNotMet_shouldReturnTrue() throws Exception {
        setupValidValidationConfig();

        when(xmlParser.getValidationConfig(any(), eq("test.namespace")))
            .thenReturn(validationConfig);
        when(conditionEvaluator.evaluate(eq(CREATE), anyString(), any()))
            .thenReturn(false);

        boolean result = validator.isValid(testDto, context);

        assertTrue(result);
        verify(ruleFactory, never()).createRule(any());
    }

    @Test
    void isValid_withConfigurationError_shouldThrowException() throws Exception {
        when(xmlParser.getValidationConfig(any(), eq("test.namespace")))
            .thenThrow(new ValidationConfigurationException("Config error"));

        assertThrows(ValidationConfigurationException.class, () ->
            validator.isValid(testDto, context)
        );
    }

    @Test
    void isValid_withNestedDto_shouldValidateNestedObject() throws Exception {
        TestDto nestedDto = new TestDto();
        nestedDto.setName("Nested");
        testDto.setNested(nestedDto);

        setupNestedValidationConfig();

        when(xmlParser.getValidationConfig(any(), eq("test.namespace")))
            .thenReturn(validationConfig);
        when(conditionEvaluator.evaluate(eq(CREATE), anyString(), any()))
            .thenReturn(true);
        when(ruleFactory.createRule(any(RuleConfig.class)))
            .thenReturn(rule);
        when(rule.validate(eq(context), any(), anyString(), anyMap()))
            .thenReturn(true);

        boolean result = validator.isValid(testDto, context);

        assertTrue(result);
    }

    @Test
    void validate_withValidRule_shouldReturnTrue() {
        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setType("required");
        ruleConfig.setParameters(Map.of());

        when(ruleFactory.createRule(ruleConfig)).thenReturn(rule);
        when(rule.validate(context, testDto, "name", Map.of())).thenReturn(true);

        boolean result = validator.validate(ruleConfig, context, testDto, "name");

        assertTrue(result);
        verify(rule).validate(context, testDto, "name", Map.of());
    }

    private void setupValidValidationConfig() {
        validationConfig = new ValidationConfig();

        FieldValidation fieldValidation = new FieldValidation();
        fieldValidation.setName("name");

        ConditionConfig conditionConfig = new ConditionConfig();
        conditionConfig.setExpression(CREATE);

        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setType("required");
        ruleConfig.setParameters(Map.of());

        conditionConfig.setRules(List.of(ruleConfig));
        fieldValidation.setConditions(List.of(conditionConfig));

        validationConfig.setFields(List.of(fieldValidation));
    }

    private void setupNestedValidationConfig() {
        validationConfig = new ValidationConfig();

        FieldValidation fieldValidation = new FieldValidation();
        fieldValidation.setName("nested");

        ValidationConfig nestedConfig = new ValidationConfig();
        FieldValidation nestedField = new FieldValidation();
        nestedField.setName("name");

        ConditionConfig conditionConfig = new ConditionConfig();
        conditionConfig.setExpression(CREATE);

        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setType("required");
        ruleConfig.setParameters(Map.of());

        conditionConfig.setRules(List.of(ruleConfig));
        nestedField.setConditions(List.of(conditionConfig));
        nestedConfig.setFields(List.of(nestedField));

        fieldValidation.setValidationConfig(nestedConfig);
        fieldValidation.setConditions(Collections.emptyList());

        validationConfig.setFields(List.of(fieldValidation));
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    static class TestDto extends AbstractBaseDto {
        private String name;
        private TestDto nested;
    }
}

