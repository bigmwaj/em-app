import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AccountDto, AccountSearchCriteria, mapAccountSearchCriteriaToHttpParams } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/account`;

  constructor(private http: HttpClient) {}

  /**
   * Gets all accounts
   */
  getAccounts(searchCriteria?: AccountSearchCriteria): Observable<SearchResult<AccountDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = mapAccountSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<AccountDto>>(this.apiUrl, { params });
  }

  /**
   * Gets a single account by ID
   */
  getAccount(id: number): Observable<AccountDto> {
    return this.http.get<AccountDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Creates a new account
   */
  createAccount(account: AccountDto): Observable<AccountDto> {
    return this.http.post<AccountDto>(this.apiUrl, account);
  }

  /**
   * Updates an existing account
   */
  updateAccount(id: number, account: AccountDto): Observable<AccountDto> {
    return this.http.put<AccountDto>(`${this.apiUrl}/${id}`, account);
  }

  /**
   * Deletes an account
   */
  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
