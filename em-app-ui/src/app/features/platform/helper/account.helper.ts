import { HttpParams } from "@angular/common/http";
import { BaseHelper } from "../../shared/base.helper";
import {
  AccountDto,
  AccountSearchCriteria,
  ContactAddressDto,
  ContactDto,
  ContactEmailDto,
  ContactPhoneDto
} from "../api.platform.model";
import { ContactHelper } from "./contact.helper";
import { AccountService } from "../service/account.service";
import { map, Observable, of } from "rxjs";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class AccountHelper extends BaseHelper<AccountDto> {

  constructor(private service: AccountService, private contactHelper: ContactHelper) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/accounts';
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<AccountDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = this.mapAccountSearchCriteriaToHttpParams(searchCriteria);
    }
    return this.service.getAccounts(params);
  }

  override getBackedDto(editMode: string, dto?: AccountDto): Observable<AccountDto> {
    var observable: Observable<AccountDto>;
    if (editMode === this.EditMode.CREATE && (!dto || !dto.id)) { // create from scratch
      observable = of({} as AccountDto);
    } else if (!dto || !dto.id) {
      throw new Error('DTO with id is required!');
    } else {
      observable = this.service.getAccount(dto.id)
        .pipe(map((fetchedDto: any) => fetchedDto.data));
    }
    return observable.pipe(
      map((fetchedDto: AccountDto) => {
        switch (editMode) {
          case this.EditMode.CREATE:
            delete fetchedDto.id;
            fetchedDto.New = true;
            break;
          case this.EditMode.CHANGE_STATUS:
            for (let key in fetchedDto) {
              if (key !== "id") {
                delete fetchedDto[key as keyof AccountDto];
              }
            }
            break;
        }
        return fetchedDto;
      })
    );
  }

  createAccountSearchCriteria(): AccountSearchCriteria {
    return {
      ...super.createDefaultSearchCriteria(),
      includeMainContact: true,
      includeContactRoles: false
    };
  }

  mapAccountSearchCriteriaToHttpParams(searchCriteria: AccountSearchCriteria): HttpParams {
    let params = super.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    if (searchCriteria.includeMainContact !== undefined) {
      params = params.set('includeMainContact', searchCriteria.includeMainContact.toString());
    }
    if (searchCriteria.includeContactRoles !== undefined) {
      params = params.set('includeContactRoles', searchCriteria.includeContactRoles.toString());
    }
    return params;
  }

  getPrimaryAccountContact(account: AccountDto): ContactDto | null {
    return account.accountContacts?.[0]?.contact || null;
  }

  getPrimaryAccountContactFullName(account: AccountDto): string | null {
    return this.contactHelper.getFullName(this.getPrimaryAccountContact(account));
  }

  getPrimaryAccountContactEmail(account: AccountDto): ContactEmailDto | null {
    return this.contactHelper.getDefaultContactEmail(this.getPrimaryAccountContact(account)) || null;
  }

  getPrimaryAccountContactPhone(account: AccountDto): ContactPhoneDto | null {
    return this.contactHelper.getDefaultContactPhone(this.getPrimaryAccountContact(account)) || null;
  }

  getPrimaryAccountContactAddress(account: AccountDto): ContactAddressDto | null {
    return this.contactHelper.getDefaultContactAddress(this.getPrimaryAccountContact(account)) || null;
  }

  buildFormData(account?: AccountDto): AccountDto {
    if (!account) {
      account = {} as AccountDto;
    }

    // Create a deep copy of the account
    const duplicatedAccount: AccountDto = JSON.parse(JSON.stringify(account));

    // Clear identifier fields
    delete duplicatedAccount.id;

    const primaryAccountContact = this.getPrimaryAccountContact(duplicatedAccount);

    // Clear IDs from nested objects
    if (primaryAccountContact) {
      delete primaryAccountContact.id;
      const defaultEmail = this.contactHelper.getDefaultContactEmail(primaryAccountContact);
      if (defaultEmail) {
        delete defaultEmail.id;
      }

      const defaultPhone = this.contactHelper.getDefaultContactPhone(primaryAccountContact);
      if (defaultPhone) {
        delete defaultPhone.id;
      }

      const defaultAddress = this.contactHelper.getDefaultContactAddress(primaryAccountContact);
      if (defaultAddress) {
        delete defaultAddress.id;
      }
    }
    return duplicatedAccount;
  }

}