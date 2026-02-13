package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

import java.util.ArrayList;
import java.util.List;

public class AccountDtoBuilder{

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final AccountDto dto = new AccountDto();

        private List<AccountContactDtoBuilder.Builder> accountContactBuilders;

        public AccountDto build() {
            if(accountContactBuilders != null && !accountContactBuilders.isEmpty()) {
                dto.setAccountContacts(new ArrayList<>());
                accountContactBuilders.stream()
                        .map(AccountContactDtoBuilder.Builder::build)
                .forEach(dto.getAccountContacts()::add);
            }
            return dto;
        }

        public Builder withDefaults(){
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setName("Test Account");
            dto.setDescription("Test Description");
            dto.setStatus(AccountStatusLvo.ACTIVE);
            dto.setAdminUsername("test");
            return this;
        }

        public Builder withDefaultAccountContactBuilder() {
            return withAccountContactBuilders(AccountContactDtoBuilder.builder().withDefaults());
        }

        public Builder withAccountContactBuilders(AccountContactDtoBuilder.Builder ... accountContactBuilders) {
            this.accountContactBuilders = List.of(accountContactBuilders);
            return this;
        }
    }
}

