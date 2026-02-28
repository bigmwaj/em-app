import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { GroupDto, GroupRoleDto, GroupUserDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria } from '../../shared/api.shared.model';
import { GroupHelper } from '../helper/group.helper';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/groups`;

  constructor(private http: HttpClient) { }

  getGroups(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<GroupDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = GroupHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<GroupDto>>(this.apiUrl, { params });
  }

  getGroup(id: number): Observable<GroupDto> {
    return this.http.get<GroupDto>(`${this.apiUrl}/id/${id}`);
  }

  createGroup(group: GroupDto): Observable<GroupDto> {
    return this.http.post<GroupDto>(this.apiUrl, group);
  }

  updateGroup(group: GroupDto): Observable<GroupDto> {
    return this.http.patch<GroupDto>(`${this.apiUrl}`, group);
  }

  deleteGroup(group: GroupDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${group.id}`);
  }

  getGroupRoles(id: number): Observable<SearchResult<GroupRoleDto>> {
    return this.http.get<SearchResult<GroupRoleDto>>(`${this.apiUrl}/id/${id}/roles`);
  }

  getGroupUsers(id: number): Observable<SearchResult<GroupUserDto>> {
    return this.http.get<SearchResult<GroupUserDto>>(`${this.apiUrl}/id/${id}/users`);
  }
}
