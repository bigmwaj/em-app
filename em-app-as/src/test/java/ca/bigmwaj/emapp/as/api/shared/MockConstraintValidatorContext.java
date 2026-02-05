package ca.bigmwaj.emapp.as.api.shared;

import jakarta.validation.ClockProvider;
import jakarta.validation.ConstraintValidatorContext;

public class MockConstraintValidatorContext implements ConstraintValidatorContext {
    @Override
    public void disableDefaultConstraintViolation() {

    }

    @Override
    public String getDefaultConstraintMessageTemplate() {
        return "";
    }

    @Override
    public ClockProvider getClockProvider() {
        return null;
    }

    @Override
    public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String s) {
        return new ConstraintViolationBuilder() {
            @Override
            public NodeBuilderDefinedContext addNode(String s) {
                return null;
            }

            @Override
            public NodeBuilderCustomizableContext addPropertyNode(String s) {
                return null;
            }

            @Override
            public LeafNodeBuilderCustomizableContext addBeanNode() {
                return null;
            }

            @Override
            public ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String s, Class<?> aClass, Integer integer) {
                return null;
            }

            @Override
            public NodeBuilderDefinedContext addParameterNode(int i) {
                return null;
            }

            @Override
            public ConstraintValidatorContext addConstraintViolation() {
                return null;
            }
        };
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
