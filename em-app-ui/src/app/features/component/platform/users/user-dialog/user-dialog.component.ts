import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User, Contact } from '../../../../models/api.platform.model';

export interface UserDialogData {
  user?: User;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss'],
  standalone: false
})
export class UserDialogComponent {
  userForm: FormGroup;
  mode: 'create' | 'edit';

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserDialogData
  ) {
    this.mode = data.mode;
    this.userForm = this.fb.group({
      username: [data.user?.username || '', Validators.required],
      email: [data.user?.email || '', [Validators.required, Validators.email]],
      name: [data.user?.name || '', Validators.required],
      status: [data.user?.status || 'ACTIVE', Validators.required],
      firstName: [data.user?.contact?.firstName || '', Validators.required],
      lastName: [data.user?.contact?.lastName || '', Validators.required],
      phone: [data.user?.contact?.phone || ''],
      company: [data.user?.contact?.company || '']
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.userForm.valid) {
      const formValue = this.userForm.value;
      const contact: Partial<Contact> = {
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        phone: formValue.phone,
        company: formValue.company
      };

      const user: Partial<User> = {
        id: this.data.user?.id,
        username: formValue.username,
        email: formValue.email,
        name: formValue.name,
        status: formValue.status,
        contact: contact as Contact
      };

      this.dialogRef.close(user);
    }
  }

  get title(): string {
    return this.mode === 'create' ? 'Create New User' : 'Edit User';
  }
}
