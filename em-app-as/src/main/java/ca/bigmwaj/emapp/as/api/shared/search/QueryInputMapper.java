package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterBy;
import ca.bigmwaj.emapp.as.dto.shared.search.SortBy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QueryInputMapper {

    Logger logger = LoggerFactory.getLogger(QueryInputMapper.class);

    QueryInputMapper INSTANCE = Mappers.getMapper(QueryInputMapper.class);

    @Mapping(source = "values", target = "values", qualifiedByName = "ValuesMapper")
    @Mapping(source = "oper", target = "oper", qualifiedByName = "operMapper")
    FilterBy toItem(FilterByInput input);

    SortBy toItem(SortByInput input);

    @Named("ValuesMapper")
    default List<?> valuesMapper(String values) {
        return Arrays.stream(values.split(",")).toList();
    }

    @Named("operMapper")
    default FilterBy.oper operMapper(String oper) {
        try {
            return FilterBy.oper.valueOf(oper);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw new PatternsConversionException(MessageConstants.MSG0004);
        }
    }
}
