import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ContactService } from '../../../service/platform/contact.service';
import { Contact } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';
import { ContactDialogComponent } from './contact-dialog/contact-dialog.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

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
    private dialog: MatDialog,
    private snackBar: MatSnackBar
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
    const dialogRef = this.dialog.open(ContactDialogComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.contactService.createContact(result).subscribe({
          next: () => {
            this.snackBar.open('Contact created successfully', 'Close', { duration: 3000 });
            this.loadContacts();
          },
          error: (err) => {
            console.error('Failed to create contact:', err);
            this.snackBar.open('Failed to create contact', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
  
  editContact(contact: Contact): void {
    const dialogRef = this.dialog.open(ContactDialogComponent, {
      width: '600px',
      data: { contact, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.id) {
        this.contactService.updateContact(result.id, result).subscribe({
          next: () => {
            this.snackBar.open('Contact updated successfully', 'Close', { duration: 3000 });
            this.loadContacts();
          },
          error: (err) => {
            console.error('Failed to update contact:', err);
            this.snackBar.open('Failed to update contact', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  deleteContact(contact: Contact): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Contact',
        message: `Are you sure you want to delete contact "${contact.firstName} ${contact.lastName}"? This action cannot be undone.`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && contact.id) {
        this.contactService.deleteContact(contact.id).subscribe({
          next: () => {
            this.snackBar.open('Contact deleted successfully', 'Close', { duration: 3000 });
            this.loadContacts();
          },
          error: (err) => {
            console.error('Failed to delete contact:', err);
            this.snackBar.open('Failed to delete contact', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
}
