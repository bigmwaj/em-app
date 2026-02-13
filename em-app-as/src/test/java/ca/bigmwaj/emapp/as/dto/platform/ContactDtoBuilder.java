package ca.bigmwaj.emapp.as.dto.platform;

import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

public class ContactDtoBuilder {
    public static ContactDtoBuilder.Builder builder() {
        return new ContactDtoBuilder.Builder();
    }

    public static class Builder {

        private final ContactDto dto = new ContactDto();

        public ContactDto build() {
            return dto;
        }

        public ContactDtoBuilder.Builder withDefaults(){
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setFirstName("Test First Name");
            dto.setLastName("Test Last Name");
            dto.setHolderType(HolderTypeLvo.ACCOUNT);
            return this;
        }
    }
}
