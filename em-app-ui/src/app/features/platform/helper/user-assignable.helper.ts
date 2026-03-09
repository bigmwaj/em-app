import { Observable, of } from "rxjs";
import { BaseHelper } from "../../shared/base.helper";
import { UserAssignableDto } from "../api.platform.model";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { Injectable } from "@angular/core";
import { GroupService } from "../service/group.service";
import { RoleService } from "../service/role.service";

@Injectable({
  providedIn: 'root'
})
export class UserAssignableHelper extends BaseHelper<UserAssignableDto> {

  constructor(private groupService: GroupService, private roleService: RoleService) {
    super();
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<UserAssignableDto>> {

    let ownerId: number | undefined;
    let ownerType: "group" | "role" | undefined;
    let params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);

    if (searchCriteria.variables) {
      ownerId = searchCriteria.variables['ownerId'];
      ownerType = searchCriteria.variables['ownerType'];
    }

    if (!ownerId) {
      return of({
        searchInfos: {
          total: 0
        },
        data: [] as UserAssignableDto[]
      } as SearchResult<UserAssignableDto>);
    }

    if (!ownerType) {
      throw new Error("ownerId and ownerType are required in searchCriteria.variables");
    }

    if (ownerType === "group") {
      return this.groupService.getGroupUsers(ownerId, params);
    } else if (ownerType === "role") {
      return this.roleService.getRoleUsers(ownerId, params);
    } else {
      throw new Error("Invalid ownerType. Expected 'group' or 'role'.");
    }
  }
}