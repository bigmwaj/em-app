import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, PageResponse } from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = `${environment.apiUrl}/api/v1/platform/user`;

  constructor(private http: HttpClient) {}

  getUsers(page: number = 0, size: number = 10): Observable<PageResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<User>>(`${this.API_URL}/search`, { params });
  }

  getUser(id: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/id/${id}`);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.API_URL, user);
  }

  updateUser(id: string, user: User): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/id/${id}`, user);
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/id/${id}`);
  }
}
