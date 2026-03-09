import { Observable, of} from "rxjs";
import { BaseHelper } from "../../shared/base.helper";
import { GroupRoleDto } from "../api.platform.model";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { Injectable } from "@angular/core";
import { GroupService } from "../service/group.service";

@Injectable({
  providedIn: 'root'
})
export class GroupRoleHelper extends BaseHelper<GroupRoleDto> {

  constructor(private service: GroupService) {
    super();
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<GroupRoleDto>> {
    let groupId: number | undefined;
    let params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);

    if (searchCriteria.variables) {
      groupId = searchCriteria.variables['groupId'];
    }

    if (!groupId) {
      return of({ data: [] as GroupRoleDto[], searchInfos: { total: 0 } } as SearchResult<GroupRoleDto>);
    }

    return this.service.getGroupRoles(groupId, params);
  }
}