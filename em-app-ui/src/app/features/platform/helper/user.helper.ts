import { SharedHelper } from "../../shared/shared.helper";
import { UserDto } from "../api.platform.model";
import { ContactHelper } from "./contact.helper";

export class UserHelper extends SharedHelper {

    static buildFormData(user?: UserDto): UserDto {
        if (!user) {
            user = {} as UserDto;
        }
        // Create a deep copy of the user
        const duplicatedUser: UserDto = JSON.parse(JSON.stringify(user));

        // Clear identifier fields
        delete duplicatedUser.id;

        // Clear IDs from nested contact object
        if (duplicatedUser.contact) {
            delete duplicatedUser.contact.id;

            const defaultEmail = ContactHelper.getDefaultContactEmail(duplicatedUser.contact);
            if (defaultEmail) {
                delete defaultEmail.id;
            }

            const defaultPhone = ContactHelper.getDefaultContactPhone(duplicatedUser.contact);
            if (defaultPhone) {
                delete defaultPhone.id;
            }

            const defaultAddress = ContactHelper.getDefaultContactAddress(duplicatedUser.contact);
            if (defaultAddress) {
                delete defaultAddress.id;
            }
        }

        return duplicatedUser;
    }

}