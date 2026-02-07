import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Account } from '../../models/api.platform.model';
import { SearchResult } from '../../models/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/account`;

  constructor(private http: HttpClient) {}

  /**
   * Gets all accounts
   */
  getAccounts(): Observable<SearchResult<Account>> {
    return this.http.get<SearchResult<Account>>(this.apiUrl);
  }

  /**
   * Gets a single account by ID
   */
  getAccount(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/${id}`);
  }

  /**
   * Creates a new account
   */
  createAccount(account: Account): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account);
  }

  /**
   * Updates an existing account
   */
  updateAccount(id: number, account: Account): Observable<Account> {
    return this.http.put<Account>(`${this.apiUrl}/${id}`, account);
  }

  /**
   * Deletes an account
   */
  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
