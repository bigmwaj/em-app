import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/api.model';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss'],
  standalone: false
})
export class AccountsComponent implements OnInit {
  accounts: Account[] = [];
  loading = true;
  error: string | null = null;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.error = null;

    this.accountService.getAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load accounts:', err);
        this.error = 'Failed to load accounts. Using sample data.';
        this.loading = false;
        // Use mock data for demonstration
        this.accounts = this.getMockAccounts();
      }
    });
  }

  private getMockAccounts(): Account[] {
    return [
      { id: 1, name: 'Acme Corporation', type: 'Business', status: 'Active', createdAt: new Date() },
      { id: 2, name: 'Tech Solutions Inc', type: 'Enterprise', status: 'Active', createdAt: new Date() },
      { id: 3, name: 'Global Services LLC', type: 'Business', status: 'Pending', createdAt: new Date() }
    ];
  }

  editAccount(account: Account): void {
    console.log('Edit account:', account);
  }

  deleteAccount(account: Account): void {
    console.log('Delete account:', account);
  }
}
