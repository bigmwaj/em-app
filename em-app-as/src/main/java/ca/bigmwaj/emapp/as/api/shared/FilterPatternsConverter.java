package ca.bigmwaj.emapp.as.api.shared;

import ca.bigmwaj.emapp.as.api.search.FilterItemInput;
import ca.bigmwaj.emapp.as.api.search.ItemMapper;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
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
public class FilterPatternsConverter {
    private static final Logger logger = LoggerFactory.getLogger(FilterPatternsConverter.class);

    private TypeDescriptor targetType;
    private String patterns;

    private static <T> Map<String, T> fetchSupportedMetadata(TypeDescriptor targetType,
                                                        Function<FilterSupportedField, T> extractor) {
        return Arrays.stream(targetType.getAnnotations())
                .filter(e -> e.annotationType().equals(ValidFilterPatterns.class))
                .map(ValidFilterPatterns.class::cast)
                .map(ValidFilterPatterns::supportedFields)
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(FilterSupportedField::name, extractor));
    }

    private Map<String, Class<?>> fetchSupportedFieldType(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, FilterSupportedField::type);
    }

    private Map<String, String> fetchSupportedRootEntityName(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, FilterSupportedField::rootEntityName);
    }

    private Map<String, String> fetchSupportedEntityFieldName(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, FilterSupportedField::entityFieldName);
    }

    List<FilterItem> convert() {
        if (patterns == null || patterns.isBlank()) {
            return Collections.emptyList();
        }

        var supportedFieldMap = fetchSupportedFieldType(targetType);
        var supportedEntityFieldNameMap = fetchSupportedEntityFieldName(targetType);
        var supportedRootEntityNameMap = fetchSupportedRootEntityName(targetType);

        return Arrays.stream(patterns.split(";"))
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .map(e -> mapToFilterItemInput(supportedEntityFieldNameMap, supportedRootEntityNameMap, e))
                .map(this::prevalidateFilterItemInput)
                .map(this::mapToFilterItem)
                .map(e -> castValues(supportedFieldMap, e))
                .toList();
    }

    private FilterItem mapToFilterItem(FilterItemInput input) {
        try {
            return ItemMapper.INSTANCE.inputToItem(input);
        } catch (MethodArgumentConversionNotSupportedException e) {
            var fi = new FilterItem();
            fi.addMessages(input.getValidationErrorMessages());
            fi.addMessage(e.getMessage());
            logger.error(e.getMessage(), e);
            return fi;
        }
    }

    private FilterItemInput mapToFilterItemInput(Map<String, String> supportedEntityFieldNameMap,
                                                 Map<String, String> supportedRootEntityNameMap,
                                                 String filterPattern) {

        var args = new ArrayDeque<>(Arrays.asList(filterPattern.split(":")));

        var builder = FilterItemInput.builder();
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

    private FilterItemInput prevalidateFilterItemInput(FilterItemInput input) {
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

    private FilterItem castValues(Map<String, Class<?>> supportedFieldMap, FilterItem filterItem) {
        var fieldName = filterItem.getName();
        if (fieldName != null && supportedFieldMap.containsKey(fieldName)) {
            var type = supportedFieldMap.get(fieldName);
            if (LocalDate.class.isAssignableFrom(type)) {
                filterItem.transformValues(LocalDate::parse);
            } else if (Enum.class.isAssignableFrom(type)) {
                filterItem.transformValues(e -> toEnum(type, e));
            }
        }
        return filterItem;
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