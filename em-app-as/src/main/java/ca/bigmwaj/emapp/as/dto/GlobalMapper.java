package ca.bigmwaj.emapp.as.dto;

import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface GlobalMapper {

    GlobalMapper INSTANCE = Mappers.getMapper(GlobalMapper.class);

    AccountContactDto toDto(AccountContactEntity entity);
    AccountContactEntity toEntity(AccountContactDto dto);

    @Mapping(target = "contacts", ignore = true)
    AccountDto toDto(AccountEntity entity);
    AccountEntity toEntity(AccountDto dto);

    @Mapping(target = "toDelete", ignore = true)
    ContactAddressDto toDto(ContactAddressEntity entity);
    ContactAddressEntity toEntity(ContactAddressDto dto);

    @Mapping(target = "toDelete", ignore = true)
    ContactEmailDto toDto(ContactEmailEntity entity);
    ContactEmailEntity toEntity(ContactEmailDto dto);

    @Mapping(target = "toDelete", ignore = true)
    ContactPhoneDto toDto(ContactPhoneEntity entity);
    ContactPhoneEntity toEntity(ContactPhoneDto dto);

    UserDto toDto(UserEntity entity);
    UserEntity toEntity(UserDto dto);

    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    ContactDto toDto(ContactEntity entity);
    ContactEntity toEntity(ContactDto dto);
}
