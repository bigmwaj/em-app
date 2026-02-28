package ca.bigmwaj.emapp.as.dto;

import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        builder = @org.mapstruct.Builder(disableBuilder = true))
public interface GlobalPlatformMapper {

    GlobalPlatformMapper INSTANCE = Mappers.getMapper(GlobalPlatformMapper.class);

    @AnyEntityToAnyDtoMapping
    DeadLetterDto toDto(DeadLetterEntity entity);
    @AnyDtoToAnyEntityMapping
    DeadLetterEntity toEntity(DeadLetterDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "accountContacts", ignore = true)
    @Mapping(target = "adminUsername", ignore = true)
    @Mapping(target = "adminUsernameType", ignore = true)
    @Mapping(target = "adminUsernameTypePhoneIndicative", ignore = true)
    AccountDto toDto(AccountEntity entity);
    @AnyDtoToAnyEntityMapping
    AccountEntity toEntity(AccountDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "accountId", source = "account", qualifiedByName = "mapAccount")
    AccountContactDto toDto(AccountContactEntity entity);
    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccountId")
    @AnyDtoToAnyEntityMapping
    AccountContactEntity toEntity(AccountContactDto dto);

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
    @Mapping(target = "userRoles", ignore = true)
    UserDto toDto(UserEntity entity);
    @AnyDtoToAnyEntityMapping
    UserEntity toEntity(UserDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    ContactDto toDto(ContactEntity entity);
    @AnyDtoToAnyEntityMapping
    ContactEntity toEntity(ContactDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "groupRoles", ignore = true)
    @Mapping(target = "groupUsers", ignore = true)
    GroupDto toDto(GroupEntity entity);
    @AnyDtoToAnyEntityMapping
    GroupEntity toEntity(GroupDto dto);

    @AnyEntityToAnyDtoMapping
    PrivilegeDto toDto(PrivilegeEntity entity);
    @AnyDtoToAnyEntityMapping
    PrivilegeEntity toEntity(PrivilegeDto dto);

    @Mapping(target = "rolePrivileges", ignore = true)
    @Mapping(target = "roleUsers", ignore = true)
    @AnyEntityToAnyDtoMapping
    RoleDto toDto(RoleEntity entity);
    @AnyDtoToAnyEntityMapping
    RoleEntity toEntity(RoleDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "groupId", source = "group", qualifiedByName = "mapGroup")
    GroupRoleDto toDto(GroupRoleEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "group", source = "groupId", qualifiedByName = "mapGroupId")
    GroupRoleEntity toEntity(GroupRoleDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "groupId", source = "group", qualifiedByName = "mapGroup")
    GroupUserDto toDto(GroupUserEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "group", source = "groupId", qualifiedByName = "mapGroupId")
    GroupUserEntity toEntity(GroupUserDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "roleId", source = "role", qualifiedByName = "mapRole")
    RolePrivilegeDto toDto(RolePrivilegeEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRoleId")
    RolePrivilegeEntity toEntity(RolePrivilegeDto dto);

    @AnyEntityToAnyDtoMapping
    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUser")
    UserRoleDto toDto(UserRoleEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserId")
    UserRoleEntity toEntity(UserRoleDto dto);

    // There is no entity for RoleUserDto. It is a reverse for UserRole.
    @AnyEntityToAnyDtoMapping
    @Mapping(target = "roleId", source = "role", qualifiedByName = "mapRole")
    RoleUserDto toVirtualDto(UserRoleEntity entity);
    @AnyDtoToAnyEntityMapping
    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRoleId")
    UserRoleEntity toEntity(RoleUserDto dto);

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
    default Short mapAccount(AccountDto account) {
        return account != null ? account.getId() : null;
    }

    @Named("mapAccountId")
    default AccountEntity mapAccountId(Short accountId) {
        if( accountId == null) {
            return null;
        }
        var account = new AccountEntity();
        account.setId(accountId);
        return account;
    }

    @Named("mapGroup")
    default Short mapGroup(GroupDto group) {
        return group != null ? group.getId() : null;
    }

    @Named("mapGroupId")
    default GroupEntity mapGroupId(Short groupId) {
        if( groupId == null) {
            return null;
        }
        var group = new GroupEntity();
        group.setId(groupId);
        return group;
    }

    @Named("mapRole")
    default Short mapRole(RoleDto role) {
        return role != null ? role.getId() : null;
    }

    @Named("mapRoleId")
    default RoleEntity mapRoleId(Short roleId) {
        if( roleId == null) {
            return null;
        }
        var role = new RoleEntity();
        role.setId(roleId);
        return role;
    }

    @Named("mapUser")
    default Short mapUser(UserDto user) {
        return user != null ? user.getId() : null;
    }

    @Named("mapUserId")
    default UserEntity mapUserId(Short userId) {
        if( userId == null) {
            return null;
        }
        var user = new UserEntity();
        user.setId(userId);
        return user;
    }

}
