package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.as.validator.shared.SupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ValidWhereClausePatterns(
        supportedFields = {
                @SupportedField(name = "name"),
                @SupportedField(name = "description"),
                @SupportedField(name = "ownerType", type = OwnerTypeLvo.class),
        })

@ValidSortByClausePatterns(
        supportedFields = {
                @SupportedField(name = "name"),
                @SupportedField(name = "description"),
                @SupportedField(name = "ownerType"),
        })
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class RoleSearchCriteria extends AbstractSearchCriteria {

    private Short assignableToGroupId;

}
