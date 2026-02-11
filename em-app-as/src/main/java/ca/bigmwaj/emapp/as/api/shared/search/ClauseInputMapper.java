package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.api.shared.converter.ClausePatternsConversionException;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClauseInputMapper {

    Logger logger = LoggerFactory.getLogger(ClauseInputMapper.class);

    ClauseInputMapper INSTANCE = Mappers.getMapper(ClauseInputMapper.class);

    @Mapping(source = "values", target = "values", qualifiedByName = "ValuesMapper")
    @Mapping(source = "oper", target = "oper", qualifiedByName = "operMapper")
    WhereClause toItem(WhereClauseInput input);

    SortByClause toItem(SortByClauseInput input);

    @Named("ValuesMapper")
    default List<?> valuesMapper(String values) {
        return Arrays.stream(values.split(",")).toList();
    }

    @Named("operMapper")
    default WhereClause.oper operMapper(String oper) {
        try {
            return WhereClause.oper.valueOf(oper);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw new ClausePatternsConversionException(MessageConstants.MSG0004);
        }
    }
}
