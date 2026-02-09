package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
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

    private static final Logger logger = LoggerFactory.getLogger(GlobalPatternsConverter.class);

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

        logger.debug("Converting sortBy patterns: {} to List<SortByClause>, SourceType:{}", source, sourceType);
        if (isValid.apply(ValidWhereClausePatterns.class::equals)) {
            return new WhereClausePatternsConverter(targetType, (String) source).convert();
        } else if (isValid.apply(ValidSortByClausePatterns.class::equals)) {
            return new SortByClausePatternsConverter(targetType, (String) source).convert();
        }

        return null;
    }

}