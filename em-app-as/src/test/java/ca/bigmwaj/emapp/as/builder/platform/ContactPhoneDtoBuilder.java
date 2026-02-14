package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactPhoneDto;
import ca.bigmwaj.emapp.dm.lvo.platform.PhoneTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

public class ContactPhoneDtoBuilder {
    public static ContactPhoneDtoBuilder.Builder builder() {
        return new ContactPhoneDtoBuilder.Builder();
    }

    public static class Builder {

        private final ContactPhoneDto dto = new ContactPhoneDto();

        public ContactPhoneDto build() {
            return dto;
        }

        public ContactPhoneDtoBuilder.Builder withDefaults(HolderTypeLvo holderType){
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setType(PhoneTypeLvo.WORK);
            dto.setPhone("Test Phone");
            dto.setHolderType(holderType);
            return this;
        }

        public ContactPhoneDtoBuilder.Builder withDefaults(){
            withDefaults(HolderTypeLvo.ACCOUNT);
            return this;
        }
    }
}
