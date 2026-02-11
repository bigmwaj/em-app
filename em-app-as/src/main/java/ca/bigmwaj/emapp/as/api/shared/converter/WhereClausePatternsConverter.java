package ca.bigmwaj.emapp.as.api.shared.converter;

import ca.bigmwaj.emapp.as.api.shared.search.ClauseInputMapper;
import ca.bigmwaj.emapp.as.api.shared.search.WhereClauseInput;
import ca.bigmwaj.emapp.as.api.shared.validator.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.api.shared.validator.WhereClauseSupportedField;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WhereClausePatternsConverter {
    private static final Logger logger = LoggerFactory.getLogger(WhereClausePatternsConverter.class);

    private TypeDescriptor targetType;

    private String patterns;

    private static <T> Map<String, T> fetchSupportedMetadata(TypeDescriptor targetType,
                                                        Function<WhereClauseSupportedField, T> extractor) {
        return Arrays.stream(targetType.getAnnotations())
                .filter(e -> e.annotationType().equals(ValidWhereClausePatterns.class))
                .map(ValidWhereClausePatterns.class::cast)
                .map(ValidWhereClausePatterns::supportedFields)
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(WhereClauseSupportedField::name, extractor));
    }

    private Map<String, Class<?>> fetchSupportedFieldType(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, WhereClauseSupportedField::type);
    }

    private Map<String, String> fetchSupportedRootEntityName(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, WhereClauseSupportedField::rootEntityName);
    }

    private Map<String, String> fetchSupportedEntityFieldName(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, WhereClauseSupportedField::entityFieldName);
    }

    List<WhereClause> convert() {
        if (patterns == null || patterns.isBlank()) {
            return Collections.emptyList();
        }

        var supportedFieldMap = fetchSupportedFieldType(targetType);
        var supportedEntityFieldNameMap = fetchSupportedEntityFieldName(targetType);
        var supportedRootEntityNameMap = fetchSupportedRootEntityName(targetType);

        return Arrays.stream(patterns.split(";"))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .map(e -> mapToFilterItemInput(supportedEntityFieldNameMap, supportedRootEntityNameMap, e))
                .map(this::prevalidateFilterItemInput)
                .map(this::mapToFilterItem)
                .map(e -> castValues(supportedFieldMap, e))
                .toList();
    }

    private WhereClause mapToFilterItem(WhereClauseInput input) {
        try {
            return ClauseInputMapper.INSTANCE.toItem(input);
        } catch (MethodArgumentConversionNotSupportedException e) {
            var fi = new WhereClause();
            fi.addMessages(input.getValidationErrorMessages());
            fi.addMessage(e.getMessage());
            logger.error(e.getMessage(), e);
            return fi;
        }
    }

    private WhereClauseInput mapToFilterItemInput(Map<String, String> supportedEntityFieldNameMap,
                                               Map<String, String> supportedRootEntityNameMap,
                                               String filterPattern) {

        var args = new ArrayDeque<>(Arrays.asList(filterPattern.split(":")));

        var builder = WhereClauseInput.builder();
        if (!args.isEmpty()) {
            var filterName = args.pollFirst();
            builder.withName(filterName);

            if (supportedEntityFieldNameMap.containsKey(filterName)) {
                var entityFieldName = supportedEntityFieldNameMap.get(filterName);
                builder.withEntityFieldName(entityFieldName);
            }

            if (supportedRootEntityNameMap.containsKey(filterName)) {
                var rootEntityName = supportedRootEntityNameMap.get(filterName);
                builder.withRootEntityName(rootEntityName);
            }
        }

        if (!args.isEmpty()) {
            builder.withOper(args.pollFirst());
        }

        if (!args.isEmpty()) {
            builder.withValues(args.pollFirst());
        }

        return builder.build();
    }

    private WhereClauseInput prevalidateFilterItemInput(WhereClauseInput input) {
        if (input.getName() == null) {
            input.addMessage(MessageConstants.MSG0001);
        }
        if (input.getOper() == null) {
            input.addMessage(MessageConstants.MSG0002);
        }
        if (input.getValues() == null) {
            input.addMessage(MessageConstants.MSG0003);
        }
        return input;
    }

    private WhereClause castValues(Map<String, Class<?>> supportedFieldMap, WhereClause filterBy) {
        var fieldName = filterBy.getName();
        if (fieldName != null && supportedFieldMap.containsKey(fieldName)) {
            var type = supportedFieldMap.get(fieldName);
            if (LocalDate.class.isAssignableFrom(type)) {
                filterBy.transformValues(LocalDate::parse);
            } else if (Enum.class.isAssignableFrom(type)) {
                filterBy.transformValues(e -> toEnum(type, e));
            }
        }
        return filterBy;
    }

    // Type safety: The cast is safe because we verify Enum.class.isAssignableFrom(type) before calling
    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E toEnum(Class<?> enumType, String value) {
        // Runtime check to ensure type safety before casting
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new IllegalArgumentException("Type must be an Enum type: " + enumType.getName());
        }
        return Enum.valueOf((Class<E>) enumType, value);
    }
}