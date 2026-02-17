import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AccountDto, AccountSearchCriteria } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';
import { PlatformHelper } from '../platform.helper';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/account`;

  constructor(private http: HttpClient) {}

  getAccounts(searchCriteria?: AccountSearchCriteria): Observable<SearchResult<AccountDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = PlatformHelper.mapAccountSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<AccountDto>>(this.apiUrl, { params });
  }

  getAccount(id: number): Observable<AccountDto> {
    return this.http.get<AccountDto>(`${this.apiUrl}/${id}`);
  }

  createAccount(account: AccountDto): Observable<AccountDto> {
    return this.http.post<AccountDto>(this.apiUrl, account);
  }

  updateAccount(account: AccountDto): Observable<AccountDto> {
    return this.http.patch<AccountDto>(`${this.apiUrl}`, account);
  }

  deleteAccount(account: AccountDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${account.id}`);
  }

}
