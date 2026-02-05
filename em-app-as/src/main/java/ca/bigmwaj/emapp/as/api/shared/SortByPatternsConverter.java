package ca.bigmwaj.emapp.as.api.shared;

import ca.bigmwaj.emapp.as.api.search.ItemMapper;
import ca.bigmwaj.emapp.as.api.search.SortByItemInput;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByItem;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SortByPatternsConverter {

    private static final Logger logger = LoggerFactory.getLogger(SortByPatternsConverter.class);

    private TypeDescriptor targetType;
    private String patterns;

    private static <T> Map<String, T> fetchSupportedMetadata(TypeDescriptor targetType,
                                                            Function<SortBySupportedField, T> extractor) {
        return Arrays.stream(targetType.getAnnotations())
                .filter(e -> e.annotationType().equals(ValidSortByPatterns.class))
                .map(ValidSortByPatterns.class::cast)
                .map(ValidSortByPatterns::supportedFields)
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(SortBySupportedField::name, extractor));
    }

     Map<String, String> fetchSupportedRootEntityName(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, SortBySupportedField::rootEntityName);
    }

     Map<String, String> fetchSupportedEntityFieldName(TypeDescriptor targetType) {
        return fetchSupportedMetadata(targetType, SortBySupportedField::entityFieldName);
    }

    List<SortByItem> convert() {
        if (patterns == null || patterns.isBlank() || targetType == null) {
            return Collections.emptyList();
        }

        var supportedEntityFieldNameMap = fetchSupportedEntityFieldName(targetType);
        var supportedRootEntityNameMap = fetchSupportedRootEntityName(targetType);

        return Arrays.stream(patterns.split(";"))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .map(e -> mapToSortByItemInput(supportedEntityFieldNameMap, supportedRootEntityNameMap, e))
                .map(this::prevalidateSortByItemInput)
                .map(this::mapToSortByItem)
                .toList();
    }

    private SortByItem mapToSortByItem(SortByItemInput input) {
        try {
            return ItemMapper.INSTANCE.inputToItem(input);
        } catch (MethodArgumentConversionNotSupportedException e) {
            var fi = new SortByItem();
            fi.addMessages(input.getValidationErrorMessages());
            fi.addMessage(e.getMessage());
            logger.error(e.getMessage(), e);
            return fi;
        }
    }

    private SortByItemInput mapToSortByItemInput(Map<String, String> supportedEntityFieldNameMap,
                                                 Map<String, String> supportedRootEntityNameMap,
                                                 String sortByPattern) {

        var args = new ArrayDeque<>(Arrays.asList(sortByPattern.split(":")));

        var builder = SortByItemInput.builder();
        if (!args.isEmpty()) {
            var name = args.pollFirst();
            builder.withName(name);

            if (supportedEntityFieldNameMap.containsKey(name)) {
                var entityFieldName = supportedEntityFieldNameMap.get(name);
                builder.withEntityFieldName(entityFieldName);
            }

            if (supportedRootEntityNameMap.containsKey(name)) {
                var rootEntityName = supportedRootEntityNameMap.get(name);
                builder.withRootEntityName(rootEntityName);
            }
        }

        if (!args.isEmpty()) {
            var sortType = args.pollFirst();
            builder.withSortType(sortType);
        }

        return builder.build();
    }

    private SortByItemInput prevalidateSortByItemInput(SortByItemInput input) {
        if (input.getName() == null) {
            input.addMessage(MessageConstants.MSG0001);
        }
        return input;
    }
}