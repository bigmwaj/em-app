import { FormGroup } from "@angular/forms";
import { SharedHelper } from "../shared/shared.helper";
import { AccountContactDto, AccountContactRoleLvo, AccountDto, AccountStatusLvo, ContactAddressDto, ContactDto, ContactEmailDto, ContactPhoneDto, HolderTypeLvo } from "./api.platform.model";

export class PlatformHelper extends SharedHelper {

    static getPrimaryAccountContact(account: AccountDto): ContactDto | null {
        return account.accountContacts?.[0]?.contact || null;
    }

    static getPrimaryAccountContactEmail(account: AccountDto): ContactEmailDto | null {
        return PlatformHelper.getPrimaryAccountContact(account)?.emails?.[0] || null;
    }

    static getPrimaryAccountContactPhone(account: AccountDto): ContactPhoneDto | null {
        return PlatformHelper.getPrimaryAccountContact(account)?.phones?.[0] || null;
    }

    static getPrimaryAccountContactAddress(account: AccountDto): ContactAddressDto | null {
        return PlatformHelper.getPrimaryAccountContact(account)?.addresses?.[0] || null;
    }

    static getDefaultContactEmail(contact: ContactDto): ContactEmailDto | null {
        return contact?.emails?.[0] || null;
    }

    static getDefaultContactPhone(contact: ContactDto): ContactPhoneDto | null {
        return contact?.phones?.[0] || null;
    }

    static getDefaultContactAddress(contact: ContactDto): ContactAddressDto | null {
        return contact?.addresses?.[0] || null;
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
            if (primaryAccountContact.mainEmail) {
                delete primaryAccountContact.mainEmail.id;
            }
            if (primaryAccountContact.mainPhone) {
                delete primaryAccountContact.mainPhone.id;
            }
            if (primaryAccountContact.mainAddress) {
                delete primaryAccountContact.mainAddress.id;
            }
        }
        return duplicatedAccount;
    }

    static buildAccountDto(detailForm: FormGroup, primaryContactForm: FormGroup,  adminUserForm: FormGroup): AccountDto {
        
        const accountFormValue = detailForm.value;
        const contactFormValue = primaryContactForm.value;

        const accountDto: AccountDto = {
            name: accountFormValue.name,
            description: accountFormValue.description,
            status: AccountStatusLvo.ACTIVE,
            accountAdminUsername: adminUserForm.value.username
        };

        // Primary contact
        const primaryContact: ContactDto = {
            firstName: contactFormValue.firstName,
            lastName: contactFormValue.lastName,
            birthDate: contactFormValue.birthDate,
            holderType: HolderTypeLvo.ACCOUNT
        };

        // Defaut email
        const defaultEmail: ContactEmailDto = {
            email: contactFormValue.mainEmail,
            type: contactFormValue.mainEmailType,             
            holderType: HolderTypeLvo.ACCOUNT,
            defaultContactPoint: true
        }

        // Defaut phone
        const defaultPhone: ContactPhoneDto = {
            phone: contactFormValue.mainPhone,
            type: contactFormValue.mainPhoneType,
            holderType: HolderTypeLvo.ACCOUNT,
            defaultContactPoint: true
        }

        // Defaut address
        const defaultAddress: ContactAddressDto = {
            address: contactFormValue.mainAddress,
            type: contactFormValue.mainAddressType,
            holderType: HolderTypeLvo.ACCOUNT,
            defaultContactPoint: true            
        }

        primaryContact.emails = [defaultEmail];
        primaryContact.phones = [defaultPhone];
        primaryContact.addresses = [defaultAddress];            

        return accountDto;
    }

}