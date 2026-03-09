import { HttpParams } from "@angular/common/http";
import { BaseHelper } from "../../shared/base.helper";
import { PrivilegeDto, PrivilegeSearchCriteria } from "../api.platform.model";
import { PrivilegeService } from "../service/privilege.service";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { SearchResult } from "../../shared/api.shared.model";

@Injectable({
  providedIn: 'root'
})
export class PrivilegeHelper extends BaseHelper<PrivilegeDto> {

  constructor(private service: PrivilegeService) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/privileges';
  }

  override search(searchCriteria: PrivilegeSearchCriteria): Observable<SearchResult<PrivilegeDto>> {
    let params = new HttpParams();  
    if (searchCriteria) {
      params = this.mapPrivilegeSearchCriteriaToHttpParams(searchCriteria);
    }   
    return this.service.getPrivileges(params);
  }

   createPrivilegeSearchCriteria(): PrivilegeSearchCriteria {
    return {
      ...super.createDefaultSearchCriteria(),
      assignableToRoleId: undefined
    };
  }
  
  mapPrivilegeSearchCriteriaToHttpParams(searchCriteria: PrivilegeSearchCriteria): HttpParams {
    let params = super.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    if (searchCriteria.assignableToRoleId !== undefined) {
      params = params.set('assignableToRoleId', searchCriteria.assignableToRoleId.toString());
    }
    return params;
  }

}