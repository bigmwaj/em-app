import { SharedHelper } from "../../shared/shared.helper";
import { ContactAddressDto, ContactDto, ContactEmailDto, ContactPhoneDto } from "../api.platform.model";

export class ContactHelper extends SharedHelper {

    static buildFormData(contact?: ContactDto): ContactDto {
        if (!contact) {
            contact = {} as ContactDto;
        }

        // Create a deep copy of the contact
        const duplicatedContact: ContactDto = JSON.parse(JSON.stringify(contact));

        // Clear identifier fields
        delete duplicatedContact.id;

        // Clear IDs from nested objects
        const defaultEmail = ContactHelper.getDefaultContactEmail(duplicatedContact);
        if (defaultEmail) {
            delete defaultEmail.id;
        }

        const defaultPhone = ContactHelper.getDefaultContactPhone(duplicatedContact);
        if (defaultPhone) {
            delete defaultPhone.id;
        }

        const defaultAddress = ContactHelper.getDefaultContactAddress(duplicatedContact);
        if (defaultAddress) {
            delete defaultAddress.id;
        }

        return duplicatedContact;
    }

    static getFullName(contact: ContactDto | null): string | null {
        if (!contact) {
            return null;
        }
        return `${contact.firstName || ''} ${contact.lastName || ''}`.trim() || null;
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
}