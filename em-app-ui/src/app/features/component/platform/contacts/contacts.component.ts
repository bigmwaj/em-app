import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ContactService } from '../../../service/platform/contact.service';
import { Contact } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';
import { DeleteDialogComponent } from '../../../../shared/dialogs/delete-dialog/delete-dialog.component';
import { ContactFormDialogComponent } from './contact-form-dialog/contact-form-dialog.component';

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

  constructor(
    private contactService: ContactService,
    private dialog: MatDialog
  ) {}

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

  createContact(): void {
    const dialogRef = this.dialog.open(ContactFormDialogComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.contactService.createContact(result).subscribe({
          next: () => {
            this.loadContacts();
          },
          error: (err) => {
            console.error('Failed to create contact:', err);
            this.error = 'Failed to create contact. Please try again.';
          }
        });
      }
    });
  }
  
  editContact(contact: Contact): void {
    const dialogRef = this.dialog.open(ContactFormDialogComponent, {
      width: '600px',
      data: { contact, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && contact.id) {
        this.contactService.updateContact(contact.id, result).subscribe({
          next: () => {
            this.loadContacts();
          },
          error: (err) => {
            console.error('Failed to update contact:', err);
            this.error = 'Failed to update contact. Please try again.';
          }
        });
      }
    });
  }

  deleteContact(contact: Contact): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '450px',
      data: {
        title: 'Delete Contact',
        message: 'Are you sure you want to delete this contact?',
        itemName: `${contact.firstName} ${contact.lastName}`
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && contact.id) {
        this.contactService.deleteContact(contact.id).subscribe({
          next: () => {
            this.loadContacts();
          },
          error: (err) => {
            console.error('Failed to delete contact:', err);
            this.error = 'Failed to delete contact. Please try again.';
          }
        });
      }
    });
  }
}
