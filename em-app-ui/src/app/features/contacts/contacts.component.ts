import { Component, OnInit } from '@angular/core';
import { ContactService } from '../../core/services/contact.service';
import { Contact } from '../../core/models/api.model';

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss'],
  standalone: false
})
export class ContactsComponent implements OnInit {
  contacts: Contact[] = [];
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
      next: (contacts) => {
        this.contacts = contacts;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load contacts:', err);
        this.error = 'Failed to load contacts. Using sample data.';
        this.loading = false;
        // Use mock data for demonstration
        this.contacts = this.getMockContacts();
      }
    });
  }

  private getMockContacts(): Contact[] {
    return [
      { id: 1, firstName: 'John', lastName: 'Doe', email: 'john.doe@example.com', phone: '555-0100', company: 'Acme Corp' },
      { id: 2, firstName: 'Jane', lastName: 'Smith', email: 'jane.smith@example.com', phone: '555-0101', company: 'Tech Solutions' },
      { id: 3, firstName: 'Bob', lastName: 'Wilson', email: 'bob.wilson@example.com', phone: '555-0102', company: 'Global Services' }
    ];
  }

  editContact(contact: Contact): void {
    console.log('Edit contact:', contact);
  }

  deleteContact(contact: Contact): void {
    console.log('Delete contact:', contact);
  }
}
