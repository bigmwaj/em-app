package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.as.validator.shared.SupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@ValidWhereClausePatterns(
        supportedFields = {
                @SupportedField(name = "id", type = Long.class),
                @SupportedField(name = "name"),
                @SupportedField(name = "status", type = AccountStatusLvo.class),
                @SupportedField(name = "firstName", rootEntityName = "c"),
                @SupportedField(name = "lastName", rootEntityName = "c"),
                @SupportedField(name = "phone", rootEntityName = "cp"),
                @SupportedField(name = "email", rootEntityName = "ce"),
                @SupportedField(name = "address", rootEntityName = "ca"),

        })

@ValidSortByClausePatterns(
        supportedFields = {
                @SupportedField(name = "id"),
                @SupportedField(name = "status"),
                @SupportedField(name = "name"),
                @SupportedField(name = "firstName", rootEntityName = "c"),
                @SupportedField(name = "lastName", rootEntityName = "c"),
                @SupportedField(name = "phone", rootEntityName = "cp"),
                @SupportedField(name = "email", rootEntityName = "ce"),
                @SupportedField(name = "address", rootEntityName = "ca"),
        })
public class AccountSearchCriteria extends AbstractSearchCriteria {

    private boolean includeMainContact;

    private boolean includeAccountContacts;

}
