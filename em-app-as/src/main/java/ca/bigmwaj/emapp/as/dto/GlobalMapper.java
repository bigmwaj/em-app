package ca.bigmwaj.emapp.as.dto;

import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface GlobalMapper {

    GlobalMapper INSTANCE = Mappers.getMapper(GlobalMapper.class);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "accountId", source = "account", qualifiedByName = "mapAccount")
    AccountContactDto toDto(AccountContactEntity entity);
    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccountId")
    @AnyDtoToAnyEntityMapping
    AccountContactEntity toEntity(AccountContactDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "accountContacts", ignore = true)
    @Mapping(target = "mainContact", ignore = true)
    AccountDto toDto(AccountEntity entity);
    @AnyDtoToAnyEntityMapping
    AccountEntity toEntity(AccountDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "contactId", source = "contact", qualifiedByName = "mapContact")
    ContactAddressDto toDto(ContactAddressEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "contact", source = "contactId", qualifiedByName = "mapContactId")
    ContactAddressEntity toEntity(ContactAddressDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "contactId", source = "contact", qualifiedByName = "mapContact")
    ContactEmailDto toDto(ContactEmailEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "contact", source = "contactId", qualifiedByName = "mapContactId")
    ContactEmailEntity toEntity(ContactEmailDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "contactId", source = "contact", qualifiedByName = "mapContact")
    ContactPhoneDto toDto(ContactPhoneEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "contact", source = "contactId", qualifiedByName = "mapContactId")
    ContactPhoneEntity toEntity(ContactPhoneDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "picture", ignore = true)
    UserDto toDto(UserEntity entity);
    @AnyDtoToAnyEntityMapping
    UserEntity toEntity(UserDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "mainEmail", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "mainPhone", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "mainAddress", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    ContactDto toDto(ContactEntity entity);
    @AnyDtoToAnyEntityMapping
    ContactEntity toEntity(ContactDto dto);

    @Named("mapContact")
    default Long mapContact(ContactDto contact) {
        return contact != null ? contact.getId() : null;
    }

    @Named("mapContactId")
    default ContactEntity mapContactId(Long contactId) {
        if( contactId == null) {
            return null;
        }
        var contact = new ContactEntity();
        contact.setId(contactId);
        return contact;
    }

    @Named("mapAccount")
    default Long mapAccount(AccountDto account) {
        return account != null ? account.getId() : null;
    }

    @Named("mapAccountId")
    default AccountEntity mapAccountId(Long accountId) {
        if( accountId == null) {
            return null;
        }
        var account = new AccountEntity();
        account.setId(accountId);
        return account;
    }

}
