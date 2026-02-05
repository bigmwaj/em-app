package ca.bigmwaj.emapp.as.api.shared;

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
public class GlobalPatternsConverter implements GenericConverter {
    Logger logger = LoggerFactory.getLogger(GlobalPatternsConverter.class);

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

        if (isValid.apply(ValidFilterPatterns.class::equals)) {
            return new FilterPatternsConverter(targetType, (String) source).convert();
        } else if (isValid.apply(ValidSortByPatterns.class::equals)) {
            return new SortByPatternsConverter(targetType, (String) source).convert();
        }

        return null;
    }
}