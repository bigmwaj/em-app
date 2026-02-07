import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Contact } from '../../../../models/api.platform.model';

export interface ContactFormDialogData {
  contact?: Contact;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-contact-form-dialog',
  templateUrl: './contact-form-dialog.component.html',
  styleUrls: ['./contact-form-dialog.component.scss'],
  standalone: false
})
export class ContactFormDialogComponent implements OnInit {
  contactForm: FormGroup;
  isEditMode: boolean;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ContactFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ContactFormDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.contactForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.contact) {
      this.contactForm.patchValue({
        firstName: this.data.contact.firstName,
        lastName: this.data.contact.lastName,
        email: this.data.contact.email,
        phone: this.data.contact.phone || '',
        company: this.data.contact.company || ''
      });
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      company: ['']
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.contactForm.valid) {
      const formValue = this.contactForm.value;
      
      const contact: Contact = {
        ...this.data.contact,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        phone: formValue.phone,
        company: formValue.company
      };

      this.dialogRef.close(contact);
    } else {
      Object.keys(this.contactForm.controls).forEach(key => {
        this.contactForm.get(key)?.markAsTouched();
      });
    }
  }
}
