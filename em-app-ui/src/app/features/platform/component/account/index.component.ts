import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountSearchCriteria, createAccountSearchCriteria } from '../../api.platform.model';
import { SearchResult, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';

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
    private router: Router
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
    console.log('Delete account:', account);
  }

  onSearch(): void {
    this.searchCriteria = createAccountSearchCriteria();
    this.searchCriteria.includeMainContact = true;
    
    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.filterByItems = [{
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

}
