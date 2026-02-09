package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(setterPrefix = "with")
public class AccountSearchCriteria extends DefaultSearchCriteria {

    private boolean includeMainContact;

    private boolean includeAccountContacts;

}
