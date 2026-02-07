import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { User } from '../../../../models/api.platform.model';

export interface UserFormDialogData {
  user?: User;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-user-form-dialog',
  templateUrl: './user-form-dialog.component.html',
  styleUrls: ['./user-form-dialog.component.scss'],
  standalone: false
})
export class UserFormDialogComponent implements OnInit {
  userForm: FormGroup;
  isEditMode: boolean;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<UserFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserFormDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.userForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.user) {
      this.userForm.patchValue({
        username: this.data.user.username,
        email: this.data.user.email,
        name: this.data.user.name,
        status: this.data.user.status || 'ACTIVE',
        firstName: this.data.user.contact?.firstName || '',
        lastName: this.data.user.contact?.lastName || '',
        phone: this.data.user.contact?.phone || '',
        company: this.data.user.contact?.company || ''
      });
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      name: ['', [Validators.required]],
      status: ['ACTIVE', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      phone: [''],
      company: ['']
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.userForm.valid) {
      const formValue = this.userForm.value;
      
      const user: User = {
        ...this.data.user,
        username: formValue.username,
        email: formValue.email,
        name: formValue.name,
        status: formValue.status,
        contact: {
          ...this.data.user?.contact,
          firstName: formValue.firstName,
          lastName: formValue.lastName,
          email: formValue.email,
          phone: formValue.phone,
          company: formValue.company
        }
      };

      this.dialogRef.close(user);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.userForm.controls).forEach(key => {
        this.userForm.get(key)?.markAsTouched();
      });
    }
  }

  getErrorMessage(fieldName: string): string {
    const control = this.userForm.get(fieldName);
    
    if (control?.hasError('required')) {
      return `${fieldName} is required`;
    }
    
    if (control?.hasError('email')) {
      return 'Please enter a valid email address';
    }
    
    if (control?.hasError('minlength')) {
      return `${fieldName} must be at least ${control.errors?.['minlength'].requiredLength} characters`;
    }
    
    return '';
  }
}
