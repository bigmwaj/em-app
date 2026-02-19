import { HttpParams } from "@angular/common/http";
import { SharedHelper } from "../../shared/shared.helper";
import {
    AccountDto,
    AccountSearchCriteria,
    ContactAddressDto,
    ContactDto,
    ContactEmailDto,
    ContactPhoneDto
} from "../api.platform.model";
import { ContactHelper } from "./contact.helper";

export class AccountHelper extends SharedHelper {

    static createAccountSearchCriteria(): AccountSearchCriteria {
        return {
            ...SharedHelper.createDefaultSearchCriteria(),
            includeMainContact: true,
            includeContactRoles: false
        };
    }

    static mapAccountSearchCriteriaToHttpParams(searchCriteria: AccountSearchCriteria): HttpParams {
        let params = SharedHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
        if (searchCriteria.includeMainContact !== undefined) {
            params = params.set('includeMainContact', searchCriteria.includeMainContact.toString());
        }
        if (searchCriteria.includeContactRoles !== undefined) {
            params = params.set('includeContactRoles', searchCriteria.includeContactRoles.toString());
        }
        return params;
    }

    static getPrimaryAccountContact(account: AccountDto): ContactDto | null {
        return account.accountContacts?.[0]?.contact || null;
    }

    static getPrimaryAccountContactFullName(account: AccountDto): string | null {
        return ContactHelper.getFullName(AccountHelper.getPrimaryAccountContact(account));
    }

    static getPrimaryAccountContactEmail(account: AccountDto): ContactEmailDto | null {
        return ContactHelper.getDefaultContactEmail(AccountHelper.getPrimaryAccountContact(account)) || null;
    }

    static getPrimaryAccountContactPhone(account: AccountDto): ContactPhoneDto | null {
        return ContactHelper.getDefaultContactPhone(AccountHelper.getPrimaryAccountContact(account)) || null;
    }

    static getPrimaryAccountContactAddress(account: AccountDto): ContactAddressDto | null {
        return ContactHelper.getDefaultContactAddress(AccountHelper.getPrimaryAccountContact(account)) || null;
    }

    static buildFormData(account?: AccountDto): AccountDto {
        if (!account) {
            account = {} as AccountDto;
        }

        // Create a deep copy of the account
        const duplicatedAccount: AccountDto = JSON.parse(JSON.stringify(account));

        // Clear identifier fields
        delete duplicatedAccount.id;

        const primaryAccountContact = AccountHelper.getPrimaryAccountContact(duplicatedAccount);

        // Clear IDs from nested objects
        if (primaryAccountContact) {
            delete primaryAccountContact.id;
            const defaultEmail = ContactHelper.getDefaultContactEmail(primaryAccountContact);
            if (defaultEmail) {
                delete defaultEmail.id;
            }

            const defaultPhone = ContactHelper.getDefaultContactPhone(primaryAccountContact);
            if (defaultPhone) {
                delete defaultPhone.id;
            }

            const defaultAddress = ContactHelper.getDefaultContactAddress(primaryAccountContact);
            if (defaultAddress) {
                delete defaultAddress.id;
            }
        }
        return duplicatedAccount;
    }

}