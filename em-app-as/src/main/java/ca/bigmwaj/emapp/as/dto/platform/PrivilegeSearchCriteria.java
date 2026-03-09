package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.validator.shared.SupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ValidSortByClausePatterns(
        supportedFields = {
                @SupportedField(name = "name"),
                @SupportedField(name = "description"),
        })

@ValidWhereClausePatterns(
        supportedFields = {
                @SupportedField(name = "name"),
                @SupportedField(name = "description"),
        })
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PrivilegeSearchCriteria extends AbstractSearchCriteria {

    private Short assignableToRoleId;

}
