import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountSearchCriteria, createAccountSearchCriteria } from '../../api.platform.model';
import { SearchResult, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { AccountChangeStatusDialogComponent } from './change-status-dialog.component';
import { AccountDeleteDialogComponent } from './delete-dialog.component';

@Component({
  selector: 'app-account-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class AccountIndexComponent extends CommonDataSource<AccountDto> implements OnInit {

  searchResult: SearchResult<AccountDto> = {} as SearchResult<AccountDto>;
  loading = true;
  message = "";
  error: string | null = null;
  searchCriteria: AccountSearchCriteria = createAccountSearchCriteria();
  displayedColumns: string[] = ['name', 'status', 'email', 'phone', 'address', 'actions'];
  searchText = '';

  constructor(
    private accountService: AccountService,
    private router: Router,
    private dialog: MatDialog
  ) {
    super();
    this.searchCriteria.includeMainContact = true;
  }

  override getKeyLabel(bean: AccountDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.error = null;

    this.accountService.getAccounts(this.searchCriteria).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load accounts:', err);
        this.error = 'Failed to load accounts. Please try again.';
        this.loading = false;
      }
    });
  }

  createAccount(): void {
    this.router.navigate(['/accounts/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  editAccount(account: AccountDto): void {
    this.router.navigate(['/accounts/edit', 'edit'], {
      state: { mode: 'edit', account: account }
    });
  }

  viewAccount(account: AccountDto): void {
    this.router.navigate(['/accounts/edit', 'view'], {
      state: { mode: 'view', account: account }
    });
  }

  deleteAccount(account: AccountDto): void {
    const dialogRef = this.dialog.open(AccountDeleteDialogComponent, {
      width: '400px',
      data: { account: account }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Refresh the accounts list after successful delete
        this.loadAccounts();
      }
    });
  }

  onSearch(): void {
    this.searchCriteria = createAccountSearchCriteria();
    this.searchCriteria.includeMainContact = true;
    
    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.whereClauses = [{
        name: 'name',
        oper: FilterOperator.LIKE,
        values: [this.searchText.trim()]
      }];
    }
    
    this.loadAccounts();
  }

  onClearSearch(): void {
    this.searchText = '';
    this.searchCriteria = createAccountSearchCriteria();
    this.searchCriteria.includeMainContact = true;
    this.loadAccounts();
  }
  

  onInitAdvancedASearch(): void {
    
  }

  duplicateAccount(account: AccountDto): void {
    // Create a deep copy of the account
    const duplicatedAccount: AccountDto = JSON.parse(JSON.stringify(account));
    
    // Clear identifier fields
    delete duplicatedAccount.id;
    
    // Clear IDs from nested objects
    if (duplicatedAccount.mainContact) {
      delete duplicatedAccount.mainContact.id;
      if (duplicatedAccount.mainContact.mainEmail) {
        delete duplicatedAccount.mainContact.mainEmail.id;
      }
      if (duplicatedAccount.mainContact.mainPhone) {
        delete duplicatedAccount.mainContact.mainPhone.id;
      }
      if (duplicatedAccount.mainContact.mainAddress) {
        delete duplicatedAccount.mainContact.mainAddress.id;
      }
    }

    // Navigate to create mode with duplicated data
    this.router.navigate(['/accounts/edit', 'create'], {
      state: { mode: 'create', account: duplicatedAccount }
    });
  }

  changeAccountStatus(account: AccountDto): void {
    const dialogRef = this.dialog.open(AccountChangeStatusDialogComponent, {
      width: '400px',
      data: { account: account }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Update the status in the account
        account.status = result;
        
        // In a real application, you would save this to the server
        // and then reload the accounts list
        // For now, just update the local data
        this.loadAccounts();
      }
    });
  }

}
