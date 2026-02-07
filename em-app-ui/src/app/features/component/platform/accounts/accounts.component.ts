import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AccountService } from '../../../service/platform/account.service';
import { Account } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';
import { DeleteDialogComponent } from '../../../../shared/dialogs/delete-dialog/delete-dialog.component';
import { AccountFormDialogComponent } from './account-form-dialog/account-form-dialog.component';

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
    private dialog: MatDialog
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
    const dialogRef = this.dialog.open(AccountFormDialogComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.accountService.createAccount(result).subscribe({
          next: () => {
            this.loadAccounts();
          },
          error: (err) => {
            console.error('Failed to create account:', err);
            this.error = 'Failed to create account. Please try again.';
          }
        });
      }
    });
  }

  editAccount(account: Account): void {
    const dialogRef = this.dialog.open(AccountFormDialogComponent, {
      width: '600px',
      data: { account, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && account.id) {
        this.accountService.updateAccount(account.id, result).subscribe({
          next: () => {
            this.loadAccounts();
          },
          error: (err) => {
            console.error('Failed to update account:', err);
            this.error = 'Failed to update account. Please try again.';
          }
        });
      }
    });
  }

  deleteAccount(account: Account): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '450px',
      data: {
        title: 'Delete Account',
        message: 'Are you sure you want to delete this account?',
        itemName: account.name
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && account.id) {
        this.accountService.deleteAccount(account.id).subscribe({
          next: () => {
            this.loadAccounts();
          },
          error: (err) => {
            console.error('Failed to delete account:', err);
            this.error = 'Failed to delete account. Please try again.';
          }
        });
      }
    });
  }
  
}
