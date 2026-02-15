import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../service/user.service';
import { UserDto } from '../../api.platform.model';
import { createDefaultSearchCriteria, DefaultSearchCriteria, SearchResult, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { PlatformHelper } from '../../platform.helper';
import { PageEvent } from '@angular/material/paginator';
import { Subject, takeUntil } from 'rxjs';
import { UserDeleteDialogComponent } from './delete-dialog.component';
import { UserChangeStatusDialogComponent } from './change-status-dialog.component';

@Component({
  selector: 'app-user-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class UserIndexComponent extends CommonDataSource<UserDto> implements OnInit, OnDestroy {
  searchResult: SearchResult<UserDto> = {} as SearchResult<UserDto>;
  loading = true;
  error: string | null = null;
  searchCriteria: DefaultSearchCriteria = createDefaultSearchCriteria();
  displayedColumns: string[] = ['holderType', 'status', 'username', 'usernameType', 'firstName', 'lastName', 'defaultEmail', 'defaultPhone', 'defaultAddress', 'actions'];
  searchText = '';
  PlatformHelper = PlatformHelper;
  private destroy$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private router: Router,
    private dialog: MatDialog
  ) {
    super();
    this.searchCriteria.pageSize = 5;
  }

  override getKeyLabel(bean: UserDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    this.loadUsers();
  }
  /**
   * Loads users from the API
   */
  loadUsers(): void {
    this.loading = true;
    this.error = null;

    this.userService.getUsers(this.searchCriteria).pipe(takeUntil(this.destroy$)).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error = 'Failed to load users. Please try again.';
        this.loading = false;
      }
    });
  }

  /**
   * Navigate to create user page
   */
  createUser(): void {
    this.router.navigate(['/users/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  /**
   * Navigate to view user page
   */
  viewUser(user: UserDto): void {
    this.router.navigate(['/users/edit', 'view'], {
      state: { mode: 'view', user: user }
    });
  }

  /**
   * Navigate to edit user page
   */
  editUser(user: UserDto): void {
    this.router.navigate(['/users/edit', 'edit'], {
      state: { mode: 'edit', user: user }
    });
  }

  /**
   * Duplicate user and navigate to create mode
   */
  duplicateUser(user: UserDto): void {
    const duplicatedUser = PlatformHelper.duplicateUser(user);
    this.router.navigate(['/users/edit', 'create'], {
      state: { mode: 'create', user: duplicatedUser }
    });
  }

  /**
   * Open delete confirmation dialog
   */
  deleteUser(user: UserDto): void {
    const dialogRef = this.dialog.open(UserDeleteDialogComponent, {
      width: '400px',
      data: { user: user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Reload the users list after successful deletion
        this.loadUsers();
      }
    });
  }

  /**
   * Open change status dialog
   */
  changeUserStatus(user: UserDto): void {
    const dialogRef = this.dialog.open(UserChangeStatusDialogComponent, {
      width: '400px',
      data: { user: user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Reload the users list after successful status change
        this.loadUsers();
      }
    });
  }

  onSearch(): void {
    this.searchCriteria = createDefaultSearchCriteria();

    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.whereClauses = [{
        name: 'firstName',
        oper: FilterOperator.LIKE,
        values: [this.searchText.trim()]
      }];
    }

    this.loadUsers();
  }

  onClearSearch(): void {
    this.searchText = '';
    this.searchCriteria = createDefaultSearchCriteria();
    this.loadUsers();
  }
}
