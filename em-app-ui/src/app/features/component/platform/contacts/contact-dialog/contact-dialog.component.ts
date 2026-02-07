import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Contact } from '../../../../models/api.platform.model';

export interface ContactDialogData {
  contact?: Contact;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-contact-dialog',
  templateUrl: './contact-dialog.component.html',
  styleUrls: ['./contact-dialog.component.scss'],
  standalone: false
})
export class ContactDialogComponent {
  contactForm: FormGroup;
  mode: 'create' | 'edit';

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ContactDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ContactDialogData
  ) {
    this.mode = data.mode;
    this.contactForm = this.fb.group({
      firstName: [data.contact?.firstName || '', Validators.required],
      lastName: [data.contact?.lastName || '', Validators.required],
      email: [data.contact?.email || '', [Validators.required, Validators.email]],
      phone: [data.contact?.phone || ''],
      company: [data.contact?.company || '']
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.contactForm.valid) {
      const contact: Partial<Contact> = {
        id: this.data.contact?.id,
        firstName: this.contactForm.value.firstName,
        lastName: this.contactForm.value.lastName,
        email: this.contactForm.value.email,
        phone: this.contactForm.value.phone,
        company: this.contactForm.value.company
      };

      this.dialogRef.close(contact);
    }
  }

  get title(): string {
    return this.mode === 'create' ? 'Create New Contact' : 'Edit Contact';
  }
}
