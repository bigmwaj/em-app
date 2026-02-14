package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

public class AccountContactDtoBuilder {
    public static AccountContactDtoBuilder.Builder builder() {
        return new AccountContactDtoBuilder.Builder();
    }

    public static class Builder {

        private final AccountContactDto dto = new AccountContactDto();

        public AccountContactDto build() {
            if (contactBuilder != null) {
                dto.setContact(contactBuilder.build());
            }
            return dto;
        }

        public AccountContactDtoBuilder.Builder withDefaults() {
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setRole(AccountContactRoleLvo.PRINCIPAL);
            return this;
        }

        private ContactDtoBuilder.Builder contactBuilder;

        public AccountContactDtoBuilder.Builder withDefaultContactBuilder() {
            return withContactBuilder(ContactDtoBuilder.builder().withDefaults());
        }

        public AccountContactDtoBuilder.Builder withContactBuilder(ContactDtoBuilder.Builder contactBuilder) {
            this.contactBuilder = contactBuilder;
            return this;
        }
    }

    public static AccountContactDtoBuilder.Builder builderWithAllDefaults() {
        return AccountContactDtoBuilder
                .builder()
                .withDefaults()
                .withContactBuilder(ContactDtoBuilder.builder().withDefaults());
    }
}
