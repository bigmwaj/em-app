package ca.bigmwaj.emapp.as.api.shared.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(setterPrefix = "with")
@Data
public class SortByClauseInput extends AbstractClauseInput {

    private String type;
}
