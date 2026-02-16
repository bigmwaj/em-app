import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../service/user.service';
import { UserDto } from '../../api.platform.model';
import { DefaultSearchCriteria, SearchResult, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { PlatformHelper } from '../../platform.helper';
import { PageEvent } from '@angular/material/paginator';
import { Subject, takeUntil } from 'rxjs';
import { UserChangeStatusDialogComponent } from './change-status-dialog.component';
import { SharedHelper } from '../../../shared/shared.helper';
import { DeleteDialogComponent } from '../../../shared/component/delete-dialog.component';
import { UserDeleteDialogComponent } from './delete-dialog.component';

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
  searchCriteria: DefaultSearchCriteria = SharedHelper.createDefaultSearchCriteria();
  displayedColumns: string[] = ['fullName', 'status', 'username', 'defaultEmail', 'defaultPhone', 'actions'];
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
    if( user.id === undefined) {
      this.error = 'User ID is missing. Cannot delete user.';
      return;
    }

    const dialogRef = this.dialog.open(UserDeleteDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm User Deletion',
        warningMessage: `Are you sure you want to delete user "${user.username}"? This action cannot be undone.`,
        //deleteAction: this.userService.deleteUser(user.id),
        user: user
      }
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
    this.searchCriteria = SharedHelper.createDefaultSearchCriteria();

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
    this.searchCriteria = SharedHelper.createDefaultSearchCriteria();
    this.loadUsers();
  }
}
