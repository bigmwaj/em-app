import { FormGroup } from "@angular/forms";
import { SharedHelper } from "../shared/shared.helper";
import {
    AccountContactDto,
    AccountContactRoleLvo,
    AccountDto,
    AccountSearchCriteria,
    AccountStatusLvo,
    ContactAddressDto,
    ContactDto,
    ContactEmailDto,
    ContactPhoneDto,
    GroupDto,
    HolderTypeLvo,
    RoleDto,
    UserDto
} from "./api.platform.model";
import { HttpParams } from "@angular/common/http";

export class PlatformHelper extends SharedHelper {

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

    static getFullName(contact: ContactDto | null): string | null {
        if (!contact) {
            return null;
        }
        return `${contact.firstName || ''} ${contact.lastName || ''}`.trim() || null;
    }

    static getPrimaryAccountContact(account: AccountDto): ContactDto | null {
        return account.accountContacts?.[0]?.contact || null;
    }

    static getPrimaryAccountContactFullName(account: AccountDto): string | null {
        return PlatformHelper.getFullName(PlatformHelper.getPrimaryAccountContact(account));
    }

    static getPrimaryAccountContactEmail(account: AccountDto): ContactEmailDto | null {
        return PlatformHelper.getDefaultContactEmail(PlatformHelper.getPrimaryAccountContact(account)) || null;
    }

    static getPrimaryAccountContactPhone(account: AccountDto): ContactPhoneDto | null {
        return PlatformHelper.getDefaultContactPhone(PlatformHelper.getPrimaryAccountContact(account)) || null;
    }

    static getPrimaryAccountContactAddress(account: AccountDto): ContactAddressDto | null {
        return PlatformHelper.getDefaultContactAddress(PlatformHelper.getPrimaryAccountContact(account)) || null;
    }

    static getDefaultContactEmail(contact: ContactDto | null): ContactEmailDto | null {
        return contact?.emails?.[0] || null;
    }

    static getDefaultContactPhone(contact: ContactDto | null): ContactPhoneDto | null {
        return contact?.phones?.[0] || null;
    }

    static getDefaultContactAddress(contact: ContactDto | null): ContactAddressDto | null {
        return contact?.addresses?.[0] || null;
    }

    static duplicateRole(role: RoleDto): RoleDto {
        // Create a deep copy of the role
        const duplicatedRole: RoleDto = JSON.parse(JSON.stringify(role));

        // Clear identifier fields
        delete duplicatedRole.id;
        return duplicatedRole;
    }
    
    static duplicateGroup(group: GroupDto): GroupDto {
        // Create a deep copy of the group
        const duplicatedGroup: GroupDto = JSON.parse(JSON.stringify(group));

        // Clear identifier fields
        delete duplicatedGroup.id;
        return duplicatedGroup;
    }

    static duplicateAccount(account: AccountDto): AccountDto {
        // Create a deep copy of the account
        const duplicatedAccount: AccountDto = JSON.parse(JSON.stringify(account));

        // Clear identifier fields
        delete duplicatedAccount.id;

        const primaryAccountContact = PlatformHelper.getPrimaryAccountContact(duplicatedAccount);

        // Clear IDs from nested objects
        if (primaryAccountContact) {
            delete primaryAccountContact.id;
            const defaultEmail = PlatformHelper.getDefaultContactEmail(primaryAccountContact);
            if (defaultEmail) {
                delete defaultEmail.id;
            }

            const defaultPhone = PlatformHelper.getDefaultContactPhone(primaryAccountContact);
            if (defaultPhone) {
                delete defaultPhone.id;
            }

            const defaultAddress = PlatformHelper.getDefaultContactAddress(primaryAccountContact);
            if (defaultAddress) {
                delete defaultAddress.id;
            }
        }
        return duplicatedAccount;
    }

    static duplicateUser(user: UserDto): UserDto {
        // Create a deep copy of the user
        const duplicatedUser: UserDto = JSON.parse(JSON.stringify(user));

        // Clear identifier fields
        delete duplicatedUser.id;

        // Clear IDs from nested contact object
        if (duplicatedUser.contact) {
            delete duplicatedUser.contact.id;

            const defaultEmail = PlatformHelper.getDefaultContactEmail(duplicatedUser.contact);
            if (defaultEmail) {
                delete defaultEmail.id;
            }

            const defaultPhone = PlatformHelper.getDefaultContactPhone(duplicatedUser.contact);
            if (defaultPhone) {
                delete defaultPhone.id;
            }

            const defaultAddress = PlatformHelper.getDefaultContactAddress(duplicatedUser.contact);
            if (defaultAddress) {
                delete defaultAddress.id;
            }
        }

        return duplicatedUser;
    }

    static duplicateContact(contact: ContactDto): ContactDto {
        // Create a deep copy of the contact
        const duplicatedContact: ContactDto = JSON.parse(JSON.stringify(contact));

        // Clear identifier fields
        delete duplicatedContact.id;

        // Clear IDs from nested objects
        const defaultEmail = PlatformHelper.getDefaultContactEmail(duplicatedContact);
        if (defaultEmail) {
            delete defaultEmail.id;
        }

        const defaultPhone = PlatformHelper.getDefaultContactPhone(duplicatedContact);
        if (defaultPhone) {
            delete defaultPhone.id;
        }

        const defaultAddress = PlatformHelper.getDefaultContactAddress(duplicatedContact);
        if (defaultAddress) {
            delete defaultAddress.id;
        }

        return duplicatedContact;
    }

}