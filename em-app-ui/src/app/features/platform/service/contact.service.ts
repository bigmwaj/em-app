import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ContactDto } from '../api.platform.model';
import { SearchResult } from '../../shared/api.shared.model';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/contacts`;

  constructor(private http: HttpClient) {}

  getContacts(params?: HttpParams): Observable<SearchResult<ContactDto>> {
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
