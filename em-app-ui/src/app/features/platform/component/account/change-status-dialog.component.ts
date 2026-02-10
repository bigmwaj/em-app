import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AccountDto, AccountStatusLvo } from '../../api.platform.model';

export interface AccountChangeStatusDialogData {
  account: AccountDto;
}

@Component({
  selector: 'app-account-change-status-dialog',
  templateUrl: './change-status-dialog.component.html',
  styleUrls: ['./change-status-dialog.component.scss'],
  standalone: false
})
export class AccountChangeStatusDialogComponent {
  selectedStatus: AccountStatusLvo;
  accountStatuses = Object.values(AccountStatusLvo);

  constructor(
    public dialogRef: MatDialogRef<AccountChangeStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountChangeStatusDialogData
  ) {
    this.selectedStatus = data.account.status || AccountStatusLvo.ACTIVE;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    this.dialogRef.close(this.selectedStatus);
  }
}
