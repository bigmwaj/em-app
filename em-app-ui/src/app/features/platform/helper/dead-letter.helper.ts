import { SharedHelper } from "../../shared/shared.helper";
import { DeadLetterDto } from "../api.platform.model";

export class DeadLetterHelper extends SharedHelper {

    static buildFormData(deadLetter?: DeadLetterDto): DeadLetterDto {
        if (!deadLetter) {
            deadLetter = {} as DeadLetterDto;
        }
        // Create a deep copy of the dead letter
        const duplicatedDeadLetter: DeadLetterDto = JSON.parse(JSON.stringify(deadLetter));

        return duplicatedDeadLetter;
    }
}