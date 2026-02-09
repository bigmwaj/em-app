import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ContactDto } from '../api.platform.model';
import { SearchResult, DefaultSearchCriteria, mapDefaultSearchCriteriaToHttpParams } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/contact`;

  constructor(private http: HttpClient) {}

  /**
   * Gets all contacts
   */
  getContacts(searchCriteria?: DefaultSearchCriteria): Observable<SearchResult<ContactDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }

    return this.http.get<SearchResult<ContactDto>>(this.apiUrl, { params });
  }

  /**
   * Gets a single contact by ID
   */
  getContact(id: number): Observable<ContactDto> {
    return this.http.get<ContactDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Creates a new contact
   */
  createContact(contact: ContactDto): Observable<ContactDto> {
    return this.http.post<ContactDto>(this.apiUrl, contact);
  }

  /**
   * Updates an existing contact
   */
  updateContact(id: number, contact: ContactDto): Observable<ContactDto> {
    return this.http.put<ContactDto>(`${this.apiUrl}/${id}`, contact);
  }

  /**
   * Deletes a contact
   */
  deleteContact(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
