import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { GroupDto, GroupRoleDto, GroupUserDto } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/groups`;

  constructor(private http: HttpClient) { }

  getGroups(params?: HttpParams): Observable<SearchResult<GroupDto>> {
    return this.http.get<SearchResult<GroupDto>>(this.apiUrl, { params });
  }

  getGroup(id: number): Observable<GroupDto> {
    return this.http.get<GroupDto>(`${this.apiUrl}/${id}`);
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

  getGroupRoles(id: number, params?: HttpParams): Observable<SearchResult<GroupRoleDto>> {
    return this.http.get<SearchResult<GroupRoleDto>>(`${this.apiUrl}/${id}/group-roles`, { params });
  }

  getGroupUsers(id: number, params?: HttpParams): Observable<SearchResult<GroupUserDto>> {
    return this.http.get<SearchResult<GroupUserDto>>(`${this.apiUrl}/${id}/group-users`, { params });
  }
}
