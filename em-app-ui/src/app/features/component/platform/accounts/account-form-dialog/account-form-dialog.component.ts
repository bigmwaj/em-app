import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Account } from '../../../../models/api.platform.model';

export interface AccountFormDialogData {
  account?: Account;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-account-form-dialog',
  templateUrl: './account-form-dialog.component.html',
  styleUrls: ['./account-form-dialog.component.scss'],
  standalone: false
})
export class AccountFormDialogComponent implements OnInit {
  accountForm: FormGroup;
  isEditMode: boolean;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AccountFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountFormDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.accountForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.account) {
      this.accountForm.patchValue({
        name: this.data.account.name,
        type: this.data.account.type,
        status: this.data.account.status || 'ACTIVE',
        description: this.data.account.description || ''
      });
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      type: ['', [Validators.required]],
      status: ['ACTIVE', [Validators.required]],
      description: ['']
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.accountForm.valid) {
      const formValue = this.accountForm.value;
      
      const account: Account = {
        ...this.data.account,
        name: formValue.name,
        type: formValue.type,
        status: formValue.status,
        description: formValue.description,
        contacts: this.data.account?.contacts || []
      };

      this.dialogRef.close(account);
    } else {
      Object.keys(this.accountForm.controls).forEach(key => {
        this.accountForm.get(key)?.markAsTouched();
      });
    }
  }
}
