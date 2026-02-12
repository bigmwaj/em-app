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
  accountContacts?: AccountContactDto[];
  mainContact?: ContactDto;
  accountAdminUsername?: string;
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
  accountId?: number;
  contact: ContactDto;
  role: AccountContactRoleLvo;
}

export interface AbstractContactPointDto extends BaseHistDto {
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

export interface UserDto extends BaseHistDto {
  id?: number;
  picture: string;
  provider: string;
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
