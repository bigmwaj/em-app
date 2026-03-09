package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.as.validator.shared.SupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ValidWhereClausePatterns(
        supportedFields = {
                @SupportedField(name = "status", type = UserStatusLvo.class),
                @SupportedField(name = "ownerType", type = OwnerTypeLvo.class),
                @SupportedField(name = "username"),
                @SupportedField(name = "firstName", rootEntityName = "c"),
                @SupportedField(name = "lastName", rootEntityName = "c"),
                @SupportedField(name = "phone", rootEntityName = "cp"),
                @SupportedField(name = "email", rootEntityName = "ce"),
                @SupportedField(name = "address", rootEntityName = "ca"),
        })

@ValidSortByClausePatterns(
        supportedFields = {
                @SupportedField(name = "status"),
                @SupportedField(name = "ownerType"),
                @SupportedField(name = "username"),
                @SupportedField(name = "firstName", rootEntityName = "c"),
                @SupportedField(name = "lastName", rootEntityName = "c"),
                @SupportedField(name = "phone", rootEntityName = "cp"),
                @SupportedField(name = "email", rootEntityName = "ce"),
                @SupportedField(name = "address", rootEntityName = "ca"),
        })
public class UserSearchCriteria extends AbstractSearchCriteria {

    private Short assignableToRoleId;

    private Short assignableToGroupId;
}
