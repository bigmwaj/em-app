import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PrivilegeDto } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class PrivilegeService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/privileges`;

  constructor(private http: HttpClient) {}

  getPrivileges(params?: HttpParams): Observable<SearchResult<PrivilegeDto>> {
    return this.http.get<SearchResult<PrivilegeDto>>(this.apiUrl, { params });
  }

  getPrivilege(id: number): Observable<PrivilegeDto> {
    return this.http.get<PrivilegeDto>(`${this.apiUrl}/${id}`);
  }
}
