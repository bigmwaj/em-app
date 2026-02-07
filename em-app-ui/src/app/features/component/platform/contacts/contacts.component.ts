import { Component, OnInit } from '@angular/core';
import { ContactService } from '../../../service/platform/contact.service';
import { Contact } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss'],
  standalone: false
})
export class ContactsComponent implements OnInit {
  searchResult: SearchResult<Contact> = {} as SearchResult<Contact>;
  loading = true;
  error: string | null = null;

  constructor(private contactService: ContactService) {}

  ngOnInit(): void {
    this.loadContacts();
  }

  loadContacts(): void {
    this.loading = true;
    this.error = null;

    this.contactService.getContacts().subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load contacts:', err);
        this.error = 'Failed to load contacts. Using sample data.';
        this.loading = false;
      }
    });
  }
  
  editContact(contact: Contact): void {
    console.log('Edit contact:', contact);
  }

  deleteContact(contact: Contact): void {
    console.log('Delete contact:', contact);
  }
}
