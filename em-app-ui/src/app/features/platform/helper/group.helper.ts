import { SharedHelper } from "../../shared/shared.helper";
import { GroupDto } from "../api.platform.model";

export class GroupHelper extends SharedHelper {

    static buildFormData(group?: GroupDto): GroupDto {
        if (!group) {
            group = {} as GroupDto;
        }
        // Create a deep copy of the group
        const duplicatedGroup: GroupDto = JSON.parse(JSON.stringify(group));

        // Clear identifier fields
        delete duplicatedGroup.id;
        return duplicatedGroup;
    }
}