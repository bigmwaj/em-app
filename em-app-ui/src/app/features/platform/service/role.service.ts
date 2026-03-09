import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { RoleDto, RolePrivilegeDto, RoleUserDto } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/roles`;

  constructor(private http: HttpClient) {}

  getRoles(params?: HttpParams): Observable<SearchResult<RoleDto>> {
    return this.http.get<SearchResult<RoleDto>>(this.apiUrl, { params });
  }

  getRole(id: number): Observable<RoleDto> {
    return this.http.get<RoleDto>(`${this.apiUrl}/${id}`);
  }
  
  getRolePrivileges(id: number, params?: HttpParams): Observable<SearchResult<RolePrivilegeDto>> {
    return this.http.get<SearchResult<RolePrivilegeDto>>(`${this.apiUrl}/${id}/role-privileges`, { params });
  }
  
  getRoleUsers(id: number, params?: HttpParams): Observable<SearchResult<RoleUserDto>> {
    return this.http.get<SearchResult<RoleUserDto>>(`${this.apiUrl}/${id}/role-users`, { params });
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
