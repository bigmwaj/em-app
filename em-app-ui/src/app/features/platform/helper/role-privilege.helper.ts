import { Observable, of} from "rxjs";
import { BaseHelper } from "../../shared/base.helper";
import { RolePrivilegeDto } from "../api.platform.model";
import { RoleService } from "../service/role.service";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class RolePrivilegeHelper extends BaseHelper<RolePrivilegeDto> {

  constructor(private service: RoleService) {
    super();
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<RolePrivilegeDto>> {
    let roleId: number | undefined;
    let params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);

    if (searchCriteria.variables) {
      roleId = searchCriteria.variables['roleId'];
    }

    if (!roleId) {
      return of({ data: [] as RolePrivilegeDto[], searchInfos: { total: 0 } } as SearchResult<RolePrivilegeDto>);
    }
    return this.service.getRolePrivileges(roleId, params);
  }
}