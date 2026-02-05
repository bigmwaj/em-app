package ca.bigmwaj.emapp.as.api.search;

import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Logger logger = LoggerFactory.getLogger(ItemMapper.class);

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "values", target = "values", qualifiedByName = "ValuesMapper")
    @Mapping(source = "oper", target = "oper", qualifiedByName = "operMapper")
    FilterItem inputToItem(FilterItemInput input);

    SortByItem inputToItem(SortByItemInput input);

    @Named("ValuesMapper")
    default List<?> valuesMapper(String values) {
        return Arrays.stream(values.split(",")).toList();
    }

    @Named("operMapper")
    default FilterItem.oper operMapper( String oper) {
        try {
            return FilterItem.oper.valueOf(oper);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw new PatternsConversionException(MessageConstants.MSG0004);
        }
    }
}
