import { HttpParams } from "@angular/common/http";
import { BaseHistDto, AbstractSearchCriteria, createDefaultSearchCriteria, mapDefaultSearchCriteriaToHttpParams } from "../shared/api.shared.model";

// Enums from ca.bigmwaj.emapp.dm.lvo.platform
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

// DTOs from ca.bigmwaj.emapp.as.dto.platform
export interface AccountDto extends BaseHistDto {
  id?: number;
  name: string;
  description?: string;
  status: AccountStatusLvo;
  contactRoles?: AccountContactDto[];
  mainContact?: ContactDto;
}

export interface ContactDto extends BaseHistDto {
  id?: number;
  firstName: string;
  lastName: string;
  birthDate?: Date;
  holderType: HolderTypeLvo;
  mainEmail?: ContactEmailDto;
  emails?: ContactEmailDto[];
  mainPhone?: ContactPhoneDto;
  phones?: ContactPhoneDto[];
  mainAddress?: ContactAddressDto;
  addresses?: ContactAddressDto[];
}

export interface AccountContactDto extends BaseHistDto {
  accountId: number;
  contact: ContactDto;
  role: AccountContactRoleLvo;
}

export interface ContactAddressDto extends BaseHistDto {
  id?: number;
  address: string;
  type: AddressTypeLvo;
  contactId: number;
  holderType: HolderTypeLvo;
}

export interface ContactEmailDto extends BaseHistDto {
  id?: number;
  email: string;
  type: EmailTypeLvo;
  contactId: number;
  holderType: HolderTypeLvo;
}

export interface ContactPhoneDto extends BaseHistDto {
  id?: number;
  phone: string;
  type: PhoneTypeLvo;
  contactId: number;
  holderType: HolderTypeLvo;
}

export interface UserDto extends BaseHistDto {
  picture: string;
  provider: string;
  id?: number;
  username: string;
  password?: string;
  contact?: ContactDto;
  status: UserStatusLvo;
  holderType: HolderTypeLvo;
}

export interface AccountSearchCriteria extends AbstractSearchCriteria {
  includeMainContact?: boolean;
  includeContactRoles?: boolean;
}

export function createAccountSearchCriteria(): AccountSearchCriteria {
  return {
    ...createDefaultSearchCriteria(),
    includeMainContact: true,
    includeContactRoles: false
  };
}

export function mapAccountSearchCriteriaToHttpParams(searchCriteria: AccountSearchCriteria): HttpParams {
  let params = mapDefaultSearchCriteriaToHttpParams(searchCriteria);
  if (searchCriteria.includeMainContact !== undefined) {
    params = params.set('includeMainContact', searchCriteria.includeMainContact.toString());
  }
  if (searchCriteria.includeContactRoles !== undefined) {
    params = params.set('includeContactRoles', searchCriteria.includeContactRoles.toString());
  }
  return params;
}

export interface UserSearchCriteria extends AbstractSearchCriteria {
  includeContact?: boolean;
}

export function createUserSearchCriteria(): UserSearchCriteria {
  return {
    ...createDefaultSearchCriteria(),
    includeContact: true
  };
}

export function mapUserSearchCriteriaToHttpParams(searchCriteria: UserSearchCriteria): HttpParams {
  let params = mapDefaultSearchCriteriaToHttpParams(searchCriteria);
  if (searchCriteria.includeContact !== undefined) {
    params = params.set('includeContact', searchCriteria.includeContact.toString());
  }
  return params;
}

export interface ContactSearchCriteria extends AbstractSearchCriteria {
  includeEmails?: boolean;
  includePhones?: boolean;
  includeAddresses?: boolean;
}

export function createContactSearchCriteria(): ContactSearchCriteria {
  return {
    ...createDefaultSearchCriteria(),
    includeEmails: true,
    includePhones: true,
    includeAddresses: true
  };
}

export function mapContactSearchCriteriaToHttpParams(searchCriteria: ContactSearchCriteria): HttpParams {
  let params = mapDefaultSearchCriteriaToHttpParams(searchCriteria);
  if (searchCriteria.includeEmails !== undefined) {
    params = params.set('includeEmails', searchCriteria.includeEmails.toString());
  }
  if (searchCriteria.includePhones !== undefined) {
    params = params.set('includePhones', searchCriteria.includePhones.toString());
  }
  if (searchCriteria.includeAddresses !== undefined) {
    params = params.set('includeAddresses', searchCriteria.includeAddresses.toString());
  }
  return params;
}