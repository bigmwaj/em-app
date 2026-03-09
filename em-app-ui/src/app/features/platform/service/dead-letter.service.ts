import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { DeadLetterDto } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class DeadLetterService {
  
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/dead-letters`;

  constructor(private http: HttpClient) { }

  getDeadLetters(params?: HttpParams): Observable<SearchResult<DeadLetterDto>> {
    return this.http.get<SearchResult<DeadLetterDto>>(this.apiUrl, { params });
  }

  getDeadLetter(id: number): Observable<DeadLetterDto> {
    return this.http.get<DeadLetterDto>(`${this.apiUrl}/${id}`);
  }

  createDeadLetter(deadLetter: DeadLetterDto): Observable<DeadLetterDto> {
    return this.http.post<DeadLetterDto>(this.apiUrl, deadLetter);
  }

  updateDeadLetter(deadLetter: DeadLetterDto): Observable<DeadLetterDto> {
    return this.http.patch<DeadLetterDto>(`${this.apiUrl}`, deadLetter);
  }

  changeDeadLetterStatus(deadLetter: DeadLetterDto): Observable<DeadLetterDto> {
    return this.http.post<DeadLetterDto>(`${this.apiUrl}/${deadLetter.id}/change-status/${deadLetter.status}`, deadLetter);
  }

  deleteDeadLetter(deadLetter: DeadLetterDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${deadLetter.id}`);
  }
}
