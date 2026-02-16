import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountSearchCriteria, createAccountSearchCriteria } from '../../api.platform.model';
import { SearchResult, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { AccountChangeStatusDialogComponent } from './change-status-dialog.component';
import { AccountDeleteDialogComponent } from './delete-dialog.component';
import { PlatformHelper } from '../../platform.helper';
import { PageData } from '../../../shared/shared.helper';
import { PageEvent } from '@angular/material/paginator';
import { Subject, takeUntil } from 'rxjs';
import { P } from '@angular/cdk/keycodes';

@Component({
  selector: 'app-account-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class AccountIndexComponent extends CommonDataSource<AccountDto> implements OnInit, OnDestroy {
  searchResult: SearchResult<AccountDto> = {} as SearchResult<AccountDto>;
  pageData: PageData = new PageData();
  searchCriteria: AccountSearchCriteria = createAccountSearchCriteria();
  displayedColumns: string[] = ['name', 'status', 'fullName', 'email', 'phone', 'address', 'actions'];  
  private sortableColumnMap: Map<string, string> = new Map([
    ['name', 'name'],
    ['status', 'status'],
    //['fullName', 'firstName'],
    //['email', 'email'],
    //['phone', 'phone'],
    //['address', 'address']
  ]);
  searchText = '';
  PlatformHelper = PlatformHelper;
  private destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private dialog: MatDialog
  ) {
    super();
    this.searchCriteria.includeMainContact = true;
    this.searchCriteria.pageSize = 5;
  }

  override getKeyLabel(bean: AccountDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadAccounts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  isSortable(fieldName: string): boolean {
    return this.sortableColumnMap.has(fieldName);
  }

  private getSortField(fieldName: string): string {
    if (!this.sortableColumnMap.has(fieldName)) {
      throw new Error(`Field "${fieldName}" is not defined in sortableColumnMap.`);
    }
    return this.sortableColumnMap.get(fieldName) as string;
  }
  
  getFieldSortIcon(fieldName: string): string {
    return PlatformHelper.getFieldSortIcon(this.searchCriteria, this.getSortField(fieldName));
  }
  
  sortBy(fieldName: string): void {
    PlatformHelper.setSortBy(this.searchCriteria, this.getSortField(fieldName));
    this.loadAccounts();
  }

  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;  
    this.loadAccounts();
  }

  loadAccounts(): void {
    const pageData = this.pageData;
    pageData.loading = true;
    pageData.error = null;

    this.accountService.getAccounts(this.searchCriteria).pipe(takeUntil(this.destroy$)).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        pageData.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load accounts:', err);
        pageData.error = 'Failed to load accounts. Please try again.';
        pageData.loading = false;
      },
      complete:() =>{        
        pageData.loading = false;
      },
    })
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
      this.searchCriteria.whereClauses = ["name", "firstName",  "lastName",  "phone",  "email", "address"]
      .map(field => {
        return {
          name: field,
          oper: FilterOperator.LIKE,
          values: [this.searchText.trim()]
        };
      });
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
    const duplicatedAccount = PlatformHelper.duplicateAccount(account);

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
        this.loadAccounts();
      }
    });
  }

}
