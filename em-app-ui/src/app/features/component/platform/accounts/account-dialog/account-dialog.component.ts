import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Account } from '../../../../models/api.platform.model';

export interface AccountDialogData {
  account?: Account;
  mode: 'create' | 'edit';
}

@Component({
  selector: 'app-account-dialog',
  templateUrl: './account-dialog.component.html',
  styleUrls: ['./account-dialog.component.scss'],
  standalone: false
})
export class AccountDialogComponent {
  accountForm: FormGroup;
  mode: 'create' | 'edit';

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AccountDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountDialogData
  ) {
    this.mode = data.mode;
    this.accountForm = this.fb.group({
      name: [data.account?.name || '', Validators.required],
      type: [data.account?.type || 'BUSINESS', Validators.required],
      status: [data.account?.status || 'ACTIVE', Validators.required]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.accountForm.valid) {
      const account: Partial<Account> = {
        id: this.data.account?.id,
        name: this.accountForm.value.name,
        type: this.accountForm.value.type,
        status: this.accountForm.value.status,
        contacts: this.data.account?.contacts || []
      };

      this.dialogRef.close(account);
    }
  }

  get title(): string {
    return this.mode === 'create' ? 'Create New Account' : 'Edit Account';
  }
}
