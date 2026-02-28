import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PrivilegeDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria } from '../../shared/api.shared.model';
import { PrivilegeHelper } from '../helper/privilege.helper';

@Injectable({
  providedIn: 'root'
})
export class PrivilegeService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/privileges`;

  constructor(private http: HttpClient) {}

  getPrivileges(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<PrivilegeDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = PrivilegeHelper.mapPrivilegeSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<PrivilegeDto>>(this.apiUrl, { params });
  }

}
