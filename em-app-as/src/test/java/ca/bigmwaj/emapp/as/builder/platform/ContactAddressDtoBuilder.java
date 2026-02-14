package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactAddressDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AddressTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

import java.time.LocalDate;

public class ContactAddressDtoBuilder {
    public static ContactAddressDtoBuilder.Builder builder() {
        return new ContactAddressDtoBuilder.Builder();
    }

    public static class Builder {

        private final ContactAddressDto dto = new ContactAddressDto();

        public ContactAddressDto build() {
            return dto;
        }

        public ContactAddressDtoBuilder.Builder withDefaults(HolderTypeLvo holderType){
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setType(AddressTypeLvo.WORK);
            dto.setAddress("Test Address");
            dto.setHolderType(holderType);
            return this;
        }

        public ContactAddressDtoBuilder.Builder withDefaults(){
            withDefaults(HolderTypeLvo.ACCOUNT);
            return this;
        }
    }
}
