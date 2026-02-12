package ca.bigmwaj.emapp.as.converter.shared;

import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class ClausePatternsConverter implements GenericConverter {

    private static final Logger logger = LoggerFactory.getLogger(ClausePatternsConverter.class);

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, List.class));
    }

    @Override
    public Object convert(
            Object source,
            TypeDescriptor sourceType,
            TypeDescriptor targetType) {

        Function<Predicate<Class<?>>, Boolean> isValid =
                p -> Arrays.stream(targetType.getAnnotations())
                        .map(Annotation::annotationType)
                        .anyMatch(p);

        if (isValid.apply(ValidWhereClausePatterns.class::equals)) {
            return new WhereClausePatternsConverter(targetType, (String) source).convert();
        } else if (isValid.apply(ValidSortByClausePatterns.class::equals)) {
            return new SortByClausePatternsConverter(targetType, (String) source).convert();
        }

        return null;
    }

}