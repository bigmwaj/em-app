import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { RoleDto, RolePrivilegeDto, RoleUserDto, UserRoleDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria } from '../../shared/api.shared.model';
import { RoleHelper } from '../helper/role.helper';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/role`;

  constructor(private http: HttpClient) {}

  getRoles(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<RoleDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = RoleHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<RoleDto>>(this.apiUrl, { params });
  }

  getRole(id: number): Observable<RoleDto> {
    return this.http.get<RoleDto>(`${this.apiUrl}/id/${id}`);
  }
  
  getRolePrivileges(id: number): Observable<SearchResult<RolePrivilegeDto>> {
    return this.http.get<SearchResult<RolePrivilegeDto>>(`${this.apiUrl}/id/${id}/privileges`);
  }
  
  getRoleUsers(id: number): Observable<SearchResult<RoleUserDto>> {
    return this.http.get<SearchResult<RoleUserDto>>(`${this.apiUrl}/id/${id}/users`);
  }

  createRole(role: RoleDto): Observable<RoleDto> {
    return this.http.post<RoleDto>(this.apiUrl, role);
  }

  updateRole(role: RoleDto): Observable<RoleDto> {
    return this.http.patch<RoleDto>(`${this.apiUrl}`, role);
  }

  deleteRole(role: RoleDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${role.id}`);
  }
}
