import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../../service/platform/account.service';
import { Account } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';

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

  constructor(private accountService: AccountService) {}

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

  editAccount(account: Account): void {
    console.log('Edit account:', account);
  }

  deleteAccount(account: Account): void {
    console.log('Delete account:', account);
  }
  
}
