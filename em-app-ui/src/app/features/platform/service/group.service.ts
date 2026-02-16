import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { GroupDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria } from '../../shared/api.shared.model';
import { SharedHelper } from '../../shared/shared.helper';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/group`;

  constructor(private http: HttpClient) {}

  /**
   * Gets all groups
   */
  getGroups(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<GroupDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = SharedHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<GroupDto>>(this.apiUrl, { params });
  }

  /**
   * Gets a single group by ID
   */
  getGroup(id: number): Observable<GroupDto> {
    return this.http.get<GroupDto>(`${this.apiUrl}/id/${id}`);
  }

  /**
   * Creates a new group
   */
  createGroup(group: GroupDto): Observable<GroupDto> {
    return this.http.post<GroupDto>(this.apiUrl, group);
  }

  /**
   * Updates an existing group
   */
  updateGroup(group: GroupDto): Observable<GroupDto> {
    return this.http.patch<GroupDto>(`${this.apiUrl}`, group);
  }

  /**
   * Deletes a group
   */
  deleteGroup(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
