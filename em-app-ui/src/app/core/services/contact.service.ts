import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Contact } from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/platform/contact`;

  constructor(private http: HttpClient) {}

  /**
   * Gets all contacts
   */
  getContacts(): Observable<Contact[]> {
    return this.http.get<Contact[]>(this.apiUrl);
  }

  /**
   * Gets a single contact by ID
   */
  getContact(id: number): Observable<Contact> {
    return this.http.get<Contact>(`${this.apiUrl}/${id}`);
  }

  /**
   * Creates a new contact
   */
  createContact(contact: Contact): Observable<Contact> {
    return this.http.post<Contact>(this.apiUrl, contact);
  }

  /**
   * Updates an existing contact
   */
  updateContact(id: number, contact: Contact): Observable<Contact> {
    return this.http.put<Contact>(`${this.apiUrl}/${id}`, contact);
  }

  /**
   * Deletes a contact
   */
  deleteContact(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
