import { BaseHistDto } from "./api.base.model";

export interface Account extends BaseHistDto {
  id?: number;
  name: string;
  type: string;
  status: string;
  contacts: AccountContact[];
}

export interface Contact extends BaseHistDto  {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  createdAt?: Date;
}

export interface AccountContact extends BaseHistDto  {
  accountId: number;
  contact: Contact;
  role: string;
}

export interface User extends BaseHistDto {
  id?: number;
  username: string;
  status: string;
  contact: Contact;
  email: string;
  name: string;
  picture?: string;
  provider?: string;
}
