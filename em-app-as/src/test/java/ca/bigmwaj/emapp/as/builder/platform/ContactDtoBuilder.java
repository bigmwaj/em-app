package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContactDtoBuilder {
    public static ContactDtoBuilder.Builder builder() {
        return new ContactDtoBuilder.Builder();
    }

    public static class Builder {

        private final ContactDto dto = new ContactDto();
        private List<ContactEmailDtoBuilder.Builder> contactEmailBuilders;
        private List<ContactPhoneDtoBuilder.Builder> contactPhoneBuilders;
        private List<ContactAddressDtoBuilder.Builder> contactAddressBuilders;

        public ContactDto build() {

            if (contactEmailBuilders != null && !contactEmailBuilders.isEmpty()) {
                dto.setEmails(new ArrayList<>());
                contactEmailBuilders.stream()
                        .map(ContactEmailDtoBuilder.Builder::build)
                        .forEach(dto.getEmails()::add);
            }

            if (contactPhoneBuilders != null && !contactPhoneBuilders.isEmpty()) {
                dto.setPhones(new ArrayList<>());
                contactPhoneBuilders.stream()
                        .map(ContactPhoneDtoBuilder.Builder::build)
                        .forEach(dto.getPhones()::add);
            }

            if (contactAddressBuilders != null && !contactAddressBuilders.isEmpty()) {
                dto.setAddresses(new ArrayList<>());
                contactAddressBuilders.stream()
                        .map(ContactAddressDtoBuilder.Builder::build)
                        .forEach(dto.getAddresses()::add);
            }
            return dto;
        }

        public ContactDtoBuilder.Builder withDefaults(){
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setFirstName("Test First Name");
            dto.setLastName("Test Last Name");
            dto.setHolderType(HolderTypeLvo.ACCOUNT);
            dto.setBirthDate(LocalDate.now().minusYears(21));

            return this;
        }

        public ContactDtoBuilder.Builder withDefaults(HolderTypeLvo holderType){
           withDefaults();
            dto.setHolderType(holderType);
            return this;
        }

        public ContactDtoBuilder.Builder withDefaultContactEmailBuilder() {
            return withContactEmailBuilders(ContactEmailDtoBuilder.builder().withDefaults());
        }

        public ContactDtoBuilder.Builder withContactEmailBuilders(ContactEmailDtoBuilder.Builder... contactEmailBuilders) {
            this.contactEmailBuilders = List.of(contactEmailBuilders);
            return this;
        }

        public ContactDtoBuilder.Builder withDefaultContactPhoneBuilder() {
            return withContactPhoneBuilders(ContactPhoneDtoBuilder.builder().withDefaults());
        }

        public ContactDtoBuilder.Builder withContactPhoneBuilders(ContactPhoneDtoBuilder.Builder... contactPhoneBuilders) {
            this.contactPhoneBuilders = List.of(contactPhoneBuilders);
            return this;
        }

        public ContactDtoBuilder.Builder withDefaultContactAddressBuilder() {
            return withContactAddressBuilders(ContactAddressDtoBuilder.builder().withDefaults());
        }

        public ContactDtoBuilder.Builder withContactAddressBuilders(ContactAddressDtoBuilder.Builder... contactAddressBuilders) {
            this.contactAddressBuilders = List.of(contactAddressBuilders);
            return this;
        }
    }

    public static ContactDtoBuilder.Builder builderWithAllDefaults() {
        return builderWithAllDefaults(HolderTypeLvo.ACCOUNT);
    }

    public static ContactDtoBuilder.Builder builderWithAllDefaults(HolderTypeLvo holderType) {
        return ContactDtoBuilder
                .builder()
                .withDefaults(holderType)
                .withContactEmailBuilders(ContactEmailDtoBuilder.builder().withDefaults(holderType))
                .withContactAddressBuilders(ContactAddressDtoBuilder.builder().withDefaults(holderType))
                .withContactPhoneBuilders(ContactPhoneDtoBuilder.builder().withDefaults(holderType));
    }

}
