import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountSearchCriteria, ContactDto, createAccountSearchCriteria } from '../../api.platform.model';
import { FilterBy, FilterOperator, SearchResult } from '../../../shared/api.shared.model';
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
    const item1 = {} as FilterBy;
    item1.name = 'id';
    item1.oper = FilterOperator.IN;
    item1.values = ['1'];

    const item2 = {} as FilterBy;
    item2.name = 'email';
    item2.oper = FilterOperator.LIKE;
    item2.values = ['alain'];
    this.searchCriteria.filterByItems = [item1, item2];
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
