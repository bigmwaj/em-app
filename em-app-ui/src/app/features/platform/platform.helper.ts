import { FormGroup } from "@angular/forms";
import { SharedHelper } from "../shared/shared.helper";
import { AccountContactDto, AccountContactRoleLvo, AccountDto, AccountStatusLvo, ContactAddressDto, ContactDto, ContactEmailDto, ContactPhoneDto, HolderTypeLvo, UserDto } from "./api.platform.model";

export class PlatformHelper extends SharedHelper {

    static getPrimaryAccountContact(account: AccountDto): ContactDto | null {
        return account.accountContacts?.[0]?.contact || null;
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

    static buildAccountDto(detailForm: FormGroup, primaryContactForm: FormGroup,  adminUserForm: FormGroup): AccountDto {
        
        const accountFormValue = detailForm.value;
        const contactFormValue = primaryContactForm.value;

        const accountDto: AccountDto = {
            name: accountFormValue.name,
            description: accountFormValue.description,
            status: AccountStatusLvo.ACTIVE,
            adminUsername: adminUserForm.value.adminUsername
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
        
        const accountContact: AccountContactDto = {
            contact: primaryContact,
            role: AccountContactRoleLvo.PRINCIPAL
        };

        accountDto.accountContacts = [accountContact];

        return accountDto;
    }

}