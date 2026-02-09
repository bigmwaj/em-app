import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountSearchCriteria, createAccountSearchCriteria } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';

@Component({
  selector: 'app-account-index',
  templateUrl: './account-index.component.html',
  styleUrls: ['./account-index.component.scss'],
  standalone: false
})
export class AccountIndexComponent extends CommonDataSource<AccountDto> implements OnInit {

  searchResult: SearchResult<AccountDto> = {} as SearchResult<AccountDto>;
  loading = true;
  message = "";
  error: string | null = null;
  searchCriteria: AccountSearchCriteria = createAccountSearchCriteria();
  displayedColumns: string[] = ['name', 'status', 'email', 'phone', 'address', 'actions'];

  constructor(private accountService: AccountService) {
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

  editAccount(account: AccountDto): void {
    console.log('Edit account:', account);
  }

  deleteAccount(account: AccountDto): void {
    console.log('Delete account:', account);
  }

}
