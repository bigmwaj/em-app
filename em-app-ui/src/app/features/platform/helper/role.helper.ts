import { SharedHelper } from "../../shared/shared.helper";
import { RoleDto, RoleSearchCriteria } from "../api.platform.model";

export class RoleHelper extends SharedHelper {

    static buildFormData(role?: RoleDto): RoleDto {
        if (!role) {
            role = {} as RoleDto;
        }
        // Create a deep copy of the role
        const duplicatedRole: RoleDto = JSON.parse(JSON.stringify(role));

        // Clear identifier fields
        delete duplicatedRole.id;
        return duplicatedRole;
    }

    static createRoleSearchCriteria(): RoleSearchCriteria {
        return {
            ...SharedHelper.createDefaultSearchCriteria(),
            assignableToGroupId: undefined
        };
    }
}