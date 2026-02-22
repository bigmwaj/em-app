import { HttpParams } from "@angular/common/http";
import { SharedHelper } from "../../shared/shared.helper";
import { PrivilegeSearchCriteria, UserDto, UserSearchCriteria } from "../api.platform.model";

export class PrivilegeHelper extends SharedHelper {

    static createPrivilegeSearchCriteria(): PrivilegeSearchCriteria {
        return {
            ...SharedHelper.createDefaultSearchCriteria(),
            assignableToRoleId: undefined
        };
    }
    
    static mapPrivilegeSearchCriteriaToHttpParams(searchCriteria: PrivilegeSearchCriteria): HttpParams {
        let params = SharedHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
        if (searchCriteria.assignableToRoleId !== undefined) {
            params = params.set('assignableToRoleId', searchCriteria.assignableToRoleId.toString());
        }
        return params;
    }

}