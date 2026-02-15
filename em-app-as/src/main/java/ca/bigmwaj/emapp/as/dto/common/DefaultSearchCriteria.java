package ca.bigmwaj.emapp.as.dto.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(setterPrefix = "with")
@NoArgsConstructor
public class DefaultSearchCriteria extends AbstractSearchCriteria {

}
