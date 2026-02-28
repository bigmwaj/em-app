import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { DeadLetterDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria } from '../../shared/api.shared.model';
import { DeadLetterHelper } from '../helper/dead-letter.helper';

@Injectable({
  providedIn: 'root'
})
export class DeadLetterService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/dead-letters`;

  constructor(private http: HttpClient) { }

  getDeadLetters(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<DeadLetterDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = DeadLetterHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<DeadLetterDto>>(this.apiUrl, { params });
  }

  getDeadLetter(id: number): Observable<DeadLetterDto> {
    return this.http.get<DeadLetterDto>(`${this.apiUrl}/id/${id}`);
  }

  createDeadLetter(deadLetter: DeadLetterDto): Observable<DeadLetterDto> {
    return this.http.post<DeadLetterDto>(this.apiUrl, deadLetter);
  }

  updateDeadLetter(deadLetter: DeadLetterDto): Observable<DeadLetterDto> {
    return this.http.patch<DeadLetterDto>(`${this.apiUrl}`, deadLetter);
  }

  deleteDeadLetter(deadLetter: DeadLetterDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${deadLetter.id}`);
  }
}
