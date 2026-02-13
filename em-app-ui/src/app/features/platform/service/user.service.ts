import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { UserDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria, mapDefaultSearchCriteriaToHttpParams } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/user`;

  constructor(private http: HttpClient) {}

  /**
   * Gets all users
   */
  getUsers(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<UserDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<UserDto>>(this.apiUrl, { params });
  }

  /**
   * Gets a single user by ID
   */
  getUser(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Creates a new user
   */
  createUser(user: UserDto): Observable<UserDto> {
    return this.http.post<UserDto>(this.apiUrl, user);
  }

  /**
   * Updates an existing user
   */
  updateUser(user: UserDto): Observable<UserDto> {
    return this.http.patch<UserDto>(`${this.apiUrl}`, user);
  }

  /**
   * Deletes a user
   */
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
