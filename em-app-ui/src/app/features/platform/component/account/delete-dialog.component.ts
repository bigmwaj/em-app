import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AccountDto } from '../../api.platform.model';
import { AccountService } from '../../service/account.service';
import { Subject, takeUntil } from 'rxjs';

export interface AccountDeleteDialogData {
  account: AccountDto;
}

@Component({
  selector: 'app-account-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class AccountDeleteDialogComponent implements OnDestroy {
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<AccountDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountDeleteDialogData,
    private accountService: AccountService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

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

    this.accountService.deleteAccount(this.data.account.id).pipe(takeUntil(this.destroy$)).subscribe({
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
