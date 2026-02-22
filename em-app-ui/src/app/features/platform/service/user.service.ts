import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { DefaultSearchCriteria, SearchResult } from '../../shared/api.shared.model';
import { UserDto } from '../api.platform.model';
import { UserHelper } from '../helper/user.helper';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/user`;

  constructor(private http: HttpClient) {}

  getUsers(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<UserDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = UserHelper.mapUserSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<UserDto>>(this.apiUrl, { params });
  }

  getUser(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.apiUrl}/${id}`);
  }

  createUser(user: UserDto): Observable<UserDto> {
    return this.http.post<UserDto>(this.apiUrl, user);
  }

  updateUser(user: UserDto): Observable<UserDto> {
    return this.http.patch<UserDto>(`${this.apiUrl}`, user);
  }

  deleteUser(user: UserDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${user.id}`);
  }
}
