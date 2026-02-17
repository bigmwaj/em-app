import {
  AbstractBaseDto,
  AbstractChangeTrackingDto,
  AbstractSearchCriteria,
  AbstractStatusTrackingDto
} from "../shared/api.shared.model";

export enum AccountContactRoleLvo {
  PRINCIPAL = 'PRINCIPAL',
  AGENT = 'AGENT'
}

export enum AccountStatusLvo {
  ACTIVE = 'ACTIVE',
  BLOCKED = 'BLOCKED'
}

export enum AddressTypeLvo {
  HOME = 'HOME',
  WORK = 'WORK'
}

export enum EmailTypeLvo {
  PERSONAL = 'PERSONAL',
  WORK = 'WORK'
}

export enum HolderTypeLvo {
  CORPORATE = 'CORPORATE',
  ACCOUNT = 'ACCOUNT'
}

export enum PhoneTypeLvo {
  MOBILE = 'MOBILE',
  HOME = 'HOME',
  WORK = 'WORK'
}

export enum UserStatusLvo {
  ACTIVE = 'ACTIVE',
  BLOCKED = 'BLOCKED'
}

export enum UsernameTypeLvo {
  BASIC = 'BASIC',
  PHONE = 'PHONE',
  EMAIL = 'EMAIL'
}

export interface AccountDto extends AbstractStatusTrackingDto<AccountStatusLvo> {
  id?: number;
  name: string;
  description?: string;
  accountContacts?: AccountContactDto[];
  adminUsername?: string;
  adminUsernameType?: UsernameTypeLvo;
}

export interface ContactDto extends AbstractChangeTrackingDto {
  id?: number;
  firstName: string;
  lastName: string;
  birthDate?: Date;
  holderType: HolderTypeLvo;
  emails?: ContactEmailDto[];
  phones?: ContactPhoneDto[];
  addresses?: ContactAddressDto[];
}

export interface AccountContactDto extends AbstractChangeTrackingDto {
  accountId?: number;
  contact: ContactDto;
  role: AccountContactRoleLvo;
}

export interface AbstractContactPointDto extends AbstractBaseDto {
  id?: number;
  contactId?: number;
  holderType: HolderTypeLvo;
  defaultContactPoint: boolean;
}

export interface ContactAddressDto extends AbstractContactPointDto {
  address: string;
  type: AddressTypeLvo;
  country?: string;
  region?: string;
  city?: string;
}

export interface ContactEmailDto extends AbstractContactPointDto {
  email: string;
  type: EmailTypeLvo;
}

export interface ContactPhoneDto extends AbstractContactPointDto {
  phone: string;
  type: PhoneTypeLvo;
}

export interface UserDto extends AbstractStatusTrackingDto<UserStatusLvo> {
  id?: number;
  picture: string;
  provider: string;
  username: string;
  usernameType: UsernameTypeLvo;
  password?: string;
  contact?: ContactDto;
  holderType: HolderTypeLvo;
  usernameVerified?: boolean;
}

export interface AccountSearchCriteria extends AbstractSearchCriteria {
  includeMainContact?: boolean;
  includeContactRoles?: boolean;
}

export interface GroupDto extends AbstractChangeTrackingDto {
  id?: number;
  name: string;
  description?: string;
  holderType: HolderTypeLvo;
  groupRoles?: GroupRoleDto[];
  groupUsers?: GroupUserDto[];

}

export interface PrivilegeDto extends AbstractChangeTrackingDto {
  id?: number;
  name: string;
  description?: string;
}

export interface RoleDto extends AbstractChangeTrackingDto {
  id?: number;
  name: string;
  description?: string;
  holderType: HolderTypeLvo;
  rolePrivileges?: RolePrivilegeDto[];
}

export interface GroupRoleDto extends AbstractChangeTrackingDto {
  groupId?: number;
  roleId?: number;
}

export interface GroupUserDto extends AbstractChangeTrackingDto {
  groupId?: number;
  userId?: number;
}

export interface RolePrivilegeDto extends AbstractChangeTrackingDto {
  roleId?: number;
  privilegeId?: number;
}

export interface UserRoleDto extends AbstractChangeTrackingDto {
  userId?: number;
  roleId?: number;
}
