import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AccountService } from '../../../service/platform/account.service';
import { Account } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';
import { AccountDialogComponent } from './account-dialog/account-dialog.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss'],
  standalone: false
})
export class AccountsComponent implements OnInit {
  searchResult: SearchResult<Account> = {} as SearchResult<Account>;
  loading = true;
  message = "";
  error: string | null = null;

  constructor(
    private accountService: AccountService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.error = null;

    this.accountService.getAccounts().subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load accounts:', err);
        this.error = 'Failed to load accounts. Please try again.';
        this.loading = false;
      }, 
      complete: () => {
        this.loading = false;
        console.log('Finished loading accounts');
      }
    });
  }

  createAccount(): void {
    const dialogRef = this.dialog.open(AccountDialogComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.accountService.createAccount(result).subscribe({
          next: () => {
            this.snackBar.open('Account created successfully', 'Close', { duration: 3000 });
            this.loadAccounts();
          },
          error: (err) => {
            console.error('Failed to create account:', err);
            this.snackBar.open('Failed to create account', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  editAccount(account: Account): void {
    const dialogRef = this.dialog.open(AccountDialogComponent, {
      width: '600px',
      data: { account, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.id) {
        this.accountService.updateAccount(result.id, result).subscribe({
          next: () => {
            this.snackBar.open('Account updated successfully', 'Close', { duration: 3000 });
            this.loadAccounts();
          },
          error: (err) => {
            console.error('Failed to update account:', err);
            this.snackBar.open('Failed to update account', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  deleteAccount(account: Account): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Account',
        message: `Are you sure you want to delete account "${account.name}"? This action cannot be undone.`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && account.id) {
        this.accountService.deleteAccount(account.id).subscribe({
          next: () => {
            this.snackBar.open('Account deleted successfully', 'Close', { duration: 3000 });
            this.loadAccounts();
          },
          error: (err) => {
            console.error('Failed to delete account:', err);
            this.snackBar.open('Failed to delete account', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
  
}
