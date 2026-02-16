import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AccountDto, AccountStatusLvo } from '../../api.platform.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../service/account.service';
import { EditActionLvo } from '../../../shared/api.shared.model';
import { Subject, takeUntil } from 'rxjs';

export interface AccountChangeStatusDialogData {
  account: AccountDto;
}

@Component({
  selector: 'app-account-change-status-dialog',
  templateUrl: './change-status-dialog.component.html',
  styleUrls: ['./change-status-dialog.component.scss'],
  standalone: false
})
export class AccountChangeStatusDialogComponent implements OnInit, OnDestroy {
  accountStatuses = Object.values(AccountStatusLvo);

  form!: FormGroup;
  account!: AccountDto;
  loading = false;
  private destroy$ = new Subject<void>();
  error: string | null = null;

  constructor(
    private accountService: AccountService,
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AccountChangeStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountChangeStatusDialogData
  ) {
    this.account = data.account;
    this.accountStatuses = this.accountStatuses.filter(e => e !== this.account.status);
  }

  ngOnInit(): void {
    this.initializeForms();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForms(): void {
    const defaultStatus = this.accountStatuses.length > 0 ? this.accountStatuses[0] : null;
    this.form = this.fb.group({
      status: [defaultStatus, Validators.required],
      statusDate: [new Date()],
      statusReason: []
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (this.form.invalid) {
      this.error = 'Please fill in all required fields in Account Change Status form.';
      return;
    }
    this.loading = true;
    this.error = null;

    this.accountService.updateAccount(this.buildAccountDto()).pipe(takeUntil(this.destroy$)).subscribe({
      next: (accountDto) => {
        this.loading = false;
        this.dialogRef.close(accountDto);
      },
      error: (err) => {
        console.error('Failed to change the account status:', err);
        this.error = 'Failed to change account status. Please try again.';
        this.loading = false;
      }
    });

  }

  buildAccountDto(): AccountDto {
    const formValue = this.form.value;
    const accountDto = {
      id: this.account.id,
      status: formValue.status,
      editAction: EditActionLvo.CHANGE_STATUS,
      statusDate: formValue.statusDate,
      statusReason: formValue.statusReason
    } as AccountDto;

    if (formValue.statusDate) {
      accountDto.statusDate = formValue.statusDate;
    }
    if (formValue.statusReason) {
      accountDto.statusReason = formValue.statusReason;
    }
    return accountDto;
  }
}
