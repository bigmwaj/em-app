import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ContactDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria } from '../../shared/api.shared.model';
import { SharedHelper } from '../../shared/shared.helper';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/contact`;

  constructor(private http: HttpClient) {}

  getContacts(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<ContactDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = SharedHelper.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<ContactDto>>(this.apiUrl, { params });
  }

  getContact(id: number): Observable<ContactDto> {
    return this.http.get<ContactDto>(`${this.apiUrl}/${id}`);
  }

  createContact(contact: ContactDto): Observable<ContactDto> {
    return this.http.post<ContactDto>(this.apiUrl, contact);
  }

  updateContact(contact: ContactDto): Observable<ContactDto> {
    return this.http.patch<ContactDto>(`${this.apiUrl}`, contact);
  }

  deleteContact(contact: ContactDto): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${contact.id}`);
  }
}
