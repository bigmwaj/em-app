import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ContactDto } from '../../api.platform.model';
import { ContactService } from '../../service/contact.service';
import { Subject, takeUntil } from 'rxjs';

export interface ContactDeleteDialogData {
  contact: ContactDto;
}

@Component({
  selector: 'app-contact-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class ContactDeleteDialogComponent implements OnDestroy {
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<ContactDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ContactDeleteDialogData,
    private contactService: ContactService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirmDelete(): void {
    if (!this.data.contact.id) {
      this.error = 'Contact ID is missing';
      return;
    }

    this.loading = true;
    this.error = null;

    this.contactService.deleteContact(this.data.contact.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true); // Return success
      },
      error: (err) => {
        console.error('Failed to delete contact:', err);
        this.error = 'Failed to delete contact. Please try again.';
        this.loading = false;
      }
    });
  }
}
