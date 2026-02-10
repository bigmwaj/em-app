import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AccountDto } from '../../api.platform.model';
import { AccountService } from '../../service/account.service';

export interface AccountDeleteDialogData {
  account: AccountDto;
}

@Component({
  selector: 'app-account-delete',
  templateUrl: './delete.component.html',
  styleUrls: ['./delete.component.scss'],
  standalone: false
})
export class AccountDeleteComponent {
  loading = false;
  error: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<AccountDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountDeleteDialogData,
    private accountService: AccountService
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirmDelete(): void {
    if (!this.data.account.id) {
      this.error = 'Account ID is missing';
      return;
    }

    this.loading = true;
    this.error = null;

    this.accountService.deleteAccount(this.data.account.id).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true); // Return success
      },
      error: (err) => {
        console.error('Failed to delete account:', err);
        this.error = 'Failed to delete account. Please try again.';
        this.loading = false;
      }
    });
  }
}
