import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AccountDto } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/accounts`;

  constructor(private http: HttpClient) {}

  getAccounts(params?: HttpParams): Observable<SearchResult<AccountDto>> {
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

  changeAccountStatus(account: AccountDto): Observable<AccountDto> {
    return this.http.post<AccountDto>(`${this.apiUrl}/${account.id}/change-status/${account.status}`, account);
  }

}
