package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactEmailDto;
import ca.bigmwaj.emapp.dm.lvo.platform.EmailTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

public class ContactEmailDtoBuilder {
    public static ContactEmailDtoBuilder.Builder builder() {
        return new ContactEmailDtoBuilder.Builder();
    }

    public static class Builder {

        private final ContactEmailDto dto = new ContactEmailDto();

        public ContactEmailDto build() {
            return dto;
        }

        public ContactEmailDtoBuilder.Builder withDefaults(){
            withDefaults(HolderTypeLvo.ACCOUNT);
            return this;
        }

        public ContactEmailDtoBuilder.Builder withDefaults(HolderTypeLvo holderType){
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setType(EmailTypeLvo.WORK);
            dto.setEmail("Test Email");
            dto.setHolderType(holderType);
            return this;
        }
    }
}
