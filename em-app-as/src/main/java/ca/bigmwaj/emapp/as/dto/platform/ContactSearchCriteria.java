package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.as.validator.shared.SupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ValidWhereClausePatterns(
        supportedFields = {
                @SupportedField(name = "id", type = Long.class),
                @SupportedField(name = "ownerType", type = OwnerTypeLvo.class),
                @SupportedField(name = "firstName"),
                @SupportedField(name = "lastName"),
                @SupportedField(name = "birthDate", type = LocalDate.class),
                @SupportedField(name = "phone", rootEntityName = "cp"),
                @SupportedField(name = "email", rootEntityName = "ce"),
                @SupportedField(name = "address", rootEntityName = "ca"),
        })

@ValidSortByClausePatterns(
        supportedFields = {
                @SupportedField(name = "id"),
                @SupportedField(name = "ownerType"),
                @SupportedField(name = "firstName"),
                @SupportedField(name = "lastName"),
                @SupportedField(name = "phone", rootEntityName = "cp"),
                @SupportedField(name = "email", rootEntityName = "ce"),
                @SupportedField(name = "address", rootEntityName = "ca"),
        })
public class ContactSearchCriteria extends AbstractSearchCriteria {

    private boolean includeMainContact;

    private boolean includeAccountContacts;

}
